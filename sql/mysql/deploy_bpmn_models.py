#!/usr/bin/env python3
"""
BPMN Model Deployment Script for China Pilotage Association OA System
Reads SIMPLE model JSON from database, generates BPMN XML, and deploys all models.
"""
import uuid
import json
import datetime
import mysql.connector

DB_CONFIG = {
    'host': 'localhost', 'port': 3306, 'user': 'root', 'password': 'root',
    'database': 'dh-oa', 'charset': 'utf8mb4', 'use_unicode': True
}

# ========== BPMN XML Generation ==========

def gen_flow_id():
    return f"sequenceFlow-{uuid.uuid4()}"

def build_user_task_xml(node_id, name, strategy, param, buttons_enabled, reason_require=False):
    """Generate XML for a UserTask (approval node)."""
    buttons = [
        ("通过", 1 in buttons_enabled), ("拒绝", 2 in buttons_enabled),
        ("转办", 3 in buttons_enabled), ("委派", 4 in buttons_enabled),
        ("加签", 5 in buttons_enabled), ("退回", 6 in buttons_enabled),
    ]
    btn_xml = "\n".join(
        f'        <flowable:buttonsSetting xmlns:flowable="http://flowable.org/bpmn" displayName="{bn}" enable="{"true" if be else "false"}" id="{bid}"></flowable:buttonsSetting>'
        for bn, be, bid in [(b[0], b[1], i+1) for i, b in enumerate(buttons)]
    )
    param_xml = f'\n        <flowable:candidateParam xmlns:flowable="http://flowable.org/bpmn"><![CDATA[{param}]]></flowable:candidateParam>' if param else ''
    return f'''    <userTask id="{node_id}" name="{name}">
      <extensionElements>
        <flowable:approveType xmlns:flowable="http://flowable.org/bpmn"><![CDATA[1]]></flowable:approveType>
        <flowable:candidateStrategy xmlns:flowable="http://flowable.org/bpmn"><![CDATA[{strategy}]]></flowable:candidateStrategy>{param_xml}
{btn_xml}
        <flowable:approveMethod xmlns:flowable="http://flowable.org/bpmn"><![CDATA[4]]></flowable:approveMethod>
        <flowable:rejectHandlerType xmlns:flowable="http://flowable.org/bpmn"><![CDATA[1]]></flowable:rejectHandlerType>
        <flowable:assignStartUserHandlerType xmlns:flowable="http://flowable.org/bpmn"><![CDATA[1]]></flowable:assignStartUserHandlerType>
        <flowable:assignEmptyHandlerType xmlns:flowable="http://flowable.org/bpmn"><![CDATA[1]]></flowable:assignEmptyHandlerType>
        <flowable:signEnable xmlns:flowable="http://flowable.org/bpmn"><![CDATA[false]]></flowable:signEnable>
        <flowable:reasonRequire xmlns:flowable="http://flowable.org/bpmn"><![CDATA[{"true" if reason_require else "false"}]]></flowable:reasonRequire>
        <flowable:nodeType xmlns:flowable="http://flowable.org/bpmn"><![CDATA[11]]></flowable:nodeType>
      </extensionElements>
      <multiInstanceLoopCharacteristics isSequential="true" flowable:collection="${{coll_userList}}">
        <loopCardinality>1</loopCardinality>
        <completionCondition>${{ nrOfCompletedInstances &gt;= nrOfInstances }}</completionCondition>
      </multiInstanceLoopCharacteristics>
    </userTask>'''

def build_start_user_xml(name="发起人"):
    """Generate XML for the StartUserNode (initiator)."""
    return f'''    <userTask id="StartUserNode" name="{name}">
      <extensionElements>
        <flowable:approveType xmlns:flowable="http://flowable.org/bpmn"><![CDATA[1]]></flowable:approveType>
        <flowable:candidateStrategy xmlns:flowable="http://flowable.org/bpmn"><![CDATA[36]]></flowable:candidateStrategy>
        <flowable:buttonsSetting xmlns:flowable="http://flowable.org/bpmn" displayName="提交" enable="true" id="1"></flowable:buttonsSetting>
        <flowable:buttonsSetting xmlns:flowable="http://flowable.org/bpmn" displayName="拒绝" enable="false" id="2"></flowable:buttonsSetting>
        <flowable:buttonsSetting xmlns:flowable="http://flowable.org/bpmn" displayName="转办" enable="false" id="3"></flowable:buttonsSetting>
        <flowable:buttonsSetting xmlns:flowable="http://flowable.org/bpmn" displayName="委派" enable="false" id="4"></flowable:buttonsSetting>
        <flowable:buttonsSetting xmlns:flowable="http://flowable.org/bpmn" displayName="加签" enable="false" id="5"></flowable:buttonsSetting>
        <flowable:buttonsSetting xmlns:flowable="http://flowable.org/bpmn" displayName="退回" enable="false" id="6"></flowable:buttonsSetting>
        <flowable:assignStartUserHandlerType xmlns:flowable="http://flowable.org/bpmn"><![CDATA[2]]></flowable:assignStartUserHandlerType>
      </extensionElements>
    </userTask>'''

def collect_nodes(simple_model):
    """Walk the SIMPLE model tree and collect all nodes, flows, and their relationships."""
    nodes = []      # list of (id, type, name, node_data)
    flows = []      # list of (source, target, flow_id, name, condition_expr)
    gateways = []   # list of (id, default_flow_id)

    def walk(node, parent_id=None):
        if node is None:
            return
        nid = node.get('id', '')
        ntype = node.get('type', 0)
        nname = node.get('name', '')

        if ntype == 10:  # StartUserNode
            nodes.append((nid, ntype, nname, node))
            child = node.get('childNode')
            if child:
                flows.append((nid, child['id'], gen_flow_id(), '', None))
                walk(child, nid)

        elif ntype == 11:  # ApprovalNode
            nodes.append((nid, ntype, nname, node))
            child = node.get('childNode')
            if child:
                flows.append((nid, child['id'], gen_flow_id(), '', None))
                walk(child, nid)

        elif ntype == 1:  # EndEvent
            nodes.append((nid, ntype, nname, node))

        elif ntype == 51:  # Gateway
            gw_id = nid
            condition_nodes = node.get('conditionNodes', [])
            default_flow_id = None
            # Find default flow
            for cn in condition_nodes:
                cs = cn.get('conditionSetting', {})
                if cs and cs.get('defaultFlow'):
                    default_flow_id = cn['id']
            if not default_flow_id and condition_nodes:
                default_flow_id = condition_nodes[-1]['id']

            gateways.append((gw_id, default_flow_id))
            nodes.append((nid, ntype, nname, node))

            # Gateway's childNode is where all branches converge
            main_child = node.get('childNode')

            # Process each condition branch
            for cn in condition_nodes:
                cs = cn.get('conditionSetting', {})
                is_default = cs.get('defaultFlow', False) if cs else False
                cond_expr = cs.get('conditionExpression') if cs else None
                cn_id = cn['id']
                cn_name = cn.get('name', '')

                branch_child = cn.get('childNode')
                if branch_child:
                    if is_default:
                        # Default flow: gateway -> branch_child directly
                        flows.append((gw_id, branch_child['id'], cn_id, cn_name, None))
                    else:
                        # Conditional flow: gateway -> branch_child with expression
                        flows.append((gw_id, branch_child['id'], cn_id, cn_name, cond_expr))
                    walk(branch_child, cn_id)
                else:
                    # Empty branch goes to main child (convergence)
                    if main_child:
                        if is_default:
                            flows.append((gw_id, main_child['id'], cn_id, cn_name, None))
                        else:
                            flows.append((gw_id, main_child['id'], cn_id, cn_name, cond_expr))

            # Walk the main child (convergence point)
            if main_child:
                walk(main_child, gw_id)

    walk(simple_model)
    return nodes, flows, gateways

def generate_bpmn_xml(process_key, process_name, simple_model):
    """Generate complete BPMN XML from a SIMPLE model JSON tree."""
    nodes, flows, gateways = collect_nodes(simple_model)

    elements = []
    all_node_ids = set()
    all_flow_elements = []

    # Generate element XML
    for nid, ntype, nname, ndata in nodes:
        all_node_ids.add(nid)
        if ntype == 10:  # StartUserNode
            elements.append(build_start_user_xml(nname))
        elif ntype == 11:  # ApprovalNode
            strategy = ndata.get('candidateStrategy', 36)
            param = ndata.get('candidateParam', '')
            reason = ndata.get('reasonRequire', False)
            elements.append(build_user_task_xml(nid, nname, strategy, param, [1,2,3,4,5,6], reason))
        elif ntype == 1:  # EndEvent
            elements.append(f'    <endEvent id="{nid}" name="{nname}"></endEvent>')

    # Generate gateway elements
    for gw_id, default_flow_id in gateways:
        elements.append(f'    <exclusiveGateway id="{gw_id}" default="{default_flow_id}"></exclusiveGateway>')

    # Generate sequence flow elements
    for src, tgt, fid, fname, cond_expr in flows:
        if cond_expr:
            cf = f'\n      <conditionExpression xsi:type="tFormalExpression"><![CDATA[{cond_expr}]]></conditionExpression>'
            elements.append(f'    <sequenceFlow id="{fid}" name="{fname}" sourceRef="{src}" targetRef="{tgt}">{cf}\n    </sequenceFlow>')
        else:
            elements.append(f'    <sequenceFlow id="{fid}" name="{fname}" sourceRef="{src}" targetRef="{tgt}"></sequenceFlow>')
        all_flow_elements.append((fid, src, tgt))

    # Generate BPMN Diagram
    shapes = []
    edges = []
    x_pos = 0
    node_positions = {}

    # Layout nodes horizontally
    ordered_nodes = []
    for nid, ntype, nname, ndata in nodes:
        ordered_nodes.append((nid, ntype))

    for nid, ntype in ordered_nodes:
        if ntype == 1:  # EndEvent
            w, h = 30, 30
        elif ntype == 51:  # Gateway
            w, h = 40, 40
        else:
            w, h = 100, 60
        y = 15 if (ntype in [1, 51]) else 0
        shapes.append(f'''      <bpmndi:BPMNShape bpmnElement="{nid}" id="BPMNShape_{nid}">
        <omgdc:Bounds height="{h}.0" width="{w}.0" x="{x_pos}.0" y="{y}.0"></omgdc:Bounds>
      </bpmndi:BPMNShape>''')
        node_positions[nid] = (x_pos, y, w, h)
        x_pos += 150

    # Layout edges
    for fid, src, tgt in all_flow_elements:
        if src in node_positions and tgt in node_positions:
            sx, sy, sw, sh = node_positions[src]
            tx, ty, tw, th = node_positions[tgt]
            x1 = sx + sw
            y1 = sy + sh // 2
            x2 = tx
            y2 = ty + th // 2
            edges.append(f'''      <bpmndi:BPMNEdge bpmnElement="{fid}" id="BPMNEdge_{fid}">
        <omgdi:waypoint x="{x1}.0" y="{y1}.0"></omgdi:waypoint>
        <omgdi:waypoint x="{x2}.0" y="{y2}.0"></omgdi:waypoint>
      </bpmndi:BPMNEdge>''')

    diagram_xml = f'''  <bpmndi:BPMNDiagram id="BPMNDiagram_{process_key}">
    <bpmndi:BPMNPlane bpmnElement="{process_key}" id="BPMNPlane_{process_key}">
{chr(10).join(shapes)}
{chr(10).join(edges)}
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>'''

    # Assemble complete BPMN XML
    return f'''<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:flowable="http://flowable.org/bpmn" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" typeLanguage="http://www.w3.org/2001/XMLSchema" expressionLanguage="http://www.w3.org/1999/XPath" targetNamespace="http://www.omg.org/spec/BPMN/20100524/MODEL">
  <process id="{process_key}" name="{process_name}" isExecutable="true">
    <startEvent id="StartEvent" name="开始"></startEvent>
{chr(10).join(elements)}
    <sequenceFlow id="{gen_flow_id()}" sourceRef="StartEvent" targetRef="StartUserNode"></sequenceFlow>
  </process>
{diagram_xml}
</definitions>'''


# ========== Model Definitions ==========

def make_approval_node(node_id, name, strategy, param, reason_require=False):
    return {
        "id": node_id, "type": 11, "name": name,
        "showText": f"指定角色ID：{param}" if strategy == 10 else "发起人的部门负责人",
        "candidateStrategy": strategy, "candidateParam": param,
        "approveType": 1, "approveMethod": 4,
        "fieldsPermission": [], "reasonRequire": reason_require,
        "buttonsSetting": [{"id": i, "displayName": n, "enable": True} for i, n in
            [(1,"通过"),(2,"拒绝"),(3,"转办"),(4,"委派"),(5,"加签"),(6,"退回")]],
        "signEnable": False, "skipExpression": "",
        "rejectHandler": {"type": 1, "returnNodeId": None},
        "timeoutHandler": {"enable": False, "type": None, "timeDuration": None, "maxRemindCount": None},
        "assignStartUserHandlerType": 1,
        "assignEmptyHandler": {"type": 1, "userIds": None},
        "taskCreateListener": {"enable": False, "path": None, "header": [], "body": []},
        "taskAssignListener": {"enable": False, "path": None, "header": [], "body": []},
        "taskCompleteListener": {"enable": False, "path": None, "header": [], "body": []},
    }

def make_end_node():
    return {"id": "EndEvent", "type": 1, "name": "结束"}

def make_gateway(gw_id, condition_nodes, main_child):
    return {"id": gw_id, "type": 51, "name": "条件分支", "childNode": main_child, "conditionNodes": condition_nodes}

def make_condition(flow_id, name, expr, is_default, child_node=None):
    cs = {"conditionType": None, "conditionExpression": None, "defaultFlow": True, "conditionGroups": None}
    if not is_default:
        cs = {"conditionType": 1, "conditionExpression": expr, "defaultFlow": False, "conditionGroups": None}
    result = {"id": flow_id, "type": 50, "name": name,
              "showText": f"表达式：{expr}" if expr else "未满足其它条件时，将进入此分支",
              "conditionSetting": cs}
    if child_node:
        result["childNode"] = child_node
    return result

def make_start_node(name="发起人", child=None):
    n = {"id": "StartUserNode", "type": 10, "name": name, "showText": "已设置",
         "fieldsPermission": [],
         "buttonsSetting": [{"id": 1, "displayName": "提交", "enable": True},
                            {"id": 2, "displayName": "拒绝", "enable": False},
                            {"id": 3, "displayName": "转办", "enable": False},
                            {"id": 4, "displayName": "委派", "enable": False},
                            {"id": 5, "displayName": "加签", "enable": False},
                            {"id": 6, "displayName": "退回", "enable": False}]}
    if child:
        n["childNode"] = child
    return n

# Role IDs: 201=会长, 202=秘书长, 203=常务副秘书长, 206=财务
# Strategy 37 = initiator's dept leader (冯晓光 as 办公室主任)

def build_contract_model():
    secretary = make_approval_node("Activity__1004", "秘书长终审", 10, "202")
    secretary["childNode"] = make_end_node()
    finance = make_approval_node("Activity__1003", "财务审核", 10, "206")
    finance["childNode"] = secretary
    deputy_sg = make_approval_node("Activity__1002", "常务副秘书长复核", 10, "203")
    deputy_sg["childNode"] = finance
    chairman = make_approval_node("Activity__1005", "会长审议", 10, "201", reason_require=True)
    gw = make_gateway("GateWay__1001", [
        make_condition("Flow__1001", "重大合同", "${contractIsMajor == true}", False, chairman),
        make_condition("Flow__1002", "普通合同", None, True),
    ], deputy_sg)
    dept_head = make_approval_node("Activity__1001", "部门负责人审批", 37, "1")
    dept_head["childNode"] = gw
    return make_start_node(child=dept_head)

def build_document_model():
    secretary = make_approval_node("Activity__2003", "秘书长签发", 10, "202")
    secretary["childNode"] = make_end_node()
    deputy_sg = make_approval_node("Activity__2002", "常务副秘书长审稿", 10, "203")
    deputy_sg["childNode"] = secretary
    chairman = make_approval_node("Activity__2004", "会长会商", 10, "201", reason_require=True)
    gw = make_gateway("GateWay__2001", [
        make_condition("Flow__2001", "重要公文", "${docIsImportant == true}", False, chairman),
        make_condition("Flow__2002", "普通公文", None, True),
    ], deputy_sg)
    dept_head = make_approval_node("Activity__2001", "部门负责人核稿", 37, "1")
    dept_head["childNode"] = gw
    return make_start_node(child=dept_head)

def build_expense_model():
    secretary = make_approval_node("Activity__3004", "秘书长审批", 10, "202")
    secretary["childNode"] = make_end_node()
    deputy_sg = make_approval_node("Activity__3003", "常务副秘书长复核", 10, "203")
    deputy_sg["childNode"] = secretary
    finance = make_approval_node("Activity__3002", "财务票据核验", 10, "206")
    finance["childNode"] = deputy_sg
    chairman = make_approval_node("Activity__3005", "会长审议", 10, "201", reason_require=True)
    gw = make_gateway("GateWay__3001", [
        make_condition("Flow__3001", "大额支出", "${expenseIsLargeAmount == true}", False, chairman),
        make_condition("Flow__3002", "普通支出", None, True),
    ], finance)
    dept_head = make_approval_node("Activity__3001", "部门负责人审核", 37, "1")
    dept_head["childNode"] = gw
    return make_start_node(child=dept_head)

def build_project_model():
    secretary = make_approval_node("Activity__4004", "秘书长立项批复", 10, "202")
    secretary["childNode"] = make_end_node()
    deputy_sg = make_approval_node("Activity__4003", "常务副秘书长复核", 10, "203")
    deputy_sg["childNode"] = secretary
    finance = make_approval_node("Activity__4002", "财务核预算", 10, "206")
    finance["childNode"] = deputy_sg
    chairman = make_approval_node("Activity__4005", "会长审议", 10, "201", reason_require=True)
    gw = make_gateway("GateWay__4001", [
        make_condition("Flow__4001", "重大项目", "${projectIsMajor == true}", False, chairman),
        make_condition("Flow__4002", "普通项目", None, True),
    ], finance)
    dept_head = make_approval_node("Activity__4001", "部门负责人审核", 37, "1")
    dept_head["childNode"] = gw
    return make_start_node(child=dept_head)

def build_incoming_model():
    secretary = make_approval_node("Activity__5002", "秘书长批示", 10, "202", reason_require=True)
    secretary["childNode"] = make_end_node()
    deputy_sg = make_approval_node("Activity__5001", "常务副秘书长审核", 10, "203")
    deputy_sg["childNode"] = secretary
    chairman = make_approval_node("Activity__5003", "会长组织会商", 10, "201", reason_require=True)
    gw = make_gateway("GateWay__5001", [
        make_condition("Flow__5001", "重要来文", "${incomingIsImportant == true}", False, chairman),
        make_condition("Flow__5002", "普通来文", None, True),
    ], deputy_sg)
    return make_start_node(name="秘书处收文登记", child=gw)

def build_travel_model():
    # Domestic path
    secretary_dom = make_approval_node("Activity__6003", "秘书长审批", 10, "202")
    secretary_dom["childNode"] = make_end_node()
    deputy_sg = make_approval_node("Activity__6002", "常务副秘书长复核", 10, "203")
    deputy_sg["childNode"] = secretary_dom
    dept_head = make_approval_node("Activity__6001", "部门负责人审核", 37, "1")
    dept_head["childNode"] = deputy_sg
    # Overseas path
    secretary_ovs = make_approval_node("Activity__6005", "秘书长审批", 10, "202")
    secretary_ovs["childNode"] = make_end_node()
    chairman = make_approval_node("Activity__6004", "会长组织会商", 10, "201", reason_require=True)
    chairman["childNode"] = secretary_ovs
    gw = make_gateway("GateWay__6001", [
        make_condition("Flow__6001", "出境差旅", "${travelIsOverseas == true}", False, chairman),
        make_condition("Flow__6002", "国内差旅", None, True),
    ], dept_head)
    return make_start_node(child=gw)

def build_seal_model():
    secretary = make_approval_node("Activity__7003", "秘书长终审", 10, "202")
    secretary["childNode"] = make_end_node()
    deputy_sg = make_approval_node("Activity__7002", "常务副秘书长复核", 10, "203")
    deputy_sg["childNode"] = secretary
    dept_head = make_approval_node("Activity__7001", "部门负责人审核", 37, "1")
    dept_head["childNode"] = deputy_sg
    return make_start_node(child=dept_head)

def build_car_model():
    secretary = make_approval_node("Activity__8003", "秘书长审批", 10, "202")
    secretary["childNode"] = make_end_node()
    deputy_sg = make_approval_node("Activity__8002", "常务副秘书长复核", 10, "203")
    deputy_sg["childNode"] = secretary
    dept_head = make_approval_node("Activity__8001", "部门负责人审核", 37, "1")
    dept_head["childNode"] = deputy_sg
    return make_start_node(child=dept_head)


# ========== Deployment ==========

MODELS = [
    ("oa_contract_bill", "OA合同审批单", "/oa/contract-bill-info", "/oa/contract/info/index.vue", build_contract_model),
    ("oa_document_dispatch_bill", "OA公文发文单", "/oa/document-dispatch-info", "/oa/document/info/index.vue", build_document_model),
    ("oa_expense_reimburse_bill", "OA费用报销单", "/oa/expense-reimburse-info", "/oa/expense/info/index.vue", build_expense_model),
    ("oa_project_initiation_bill", "OA项目立项单", "/oa/project-initiation-info", "/oa/project/info/index.vue", build_project_model),
    ("oa_incoming_document_bill", "OA收文办理单", "/oa/incoming-document-info", "/oa/incoming/info/index.vue", build_incoming_model),
    ("oa_travel_apply_bill", "OA差旅申请单", "/oa/travel-apply-info", "/oa/travel/info/index.vue", build_travel_model),
    ("oa_seal_apply_bill", "OA用印申请单", "/oa/seal/seal-apply-info", "/oa/seal/sealapply/info/index.vue", build_seal_model),
    ("oa_car_apply_bill", "OA用车申请单", "/oa/car/car-apply-info", "/oa/car/carapply/info/index.vue", build_car_model),
]

def deploy_all():
    conn = mysql.connector.connect(**DB_CONFIG)
    cur = conn.cursor()
    now = datetime.datetime.now()
    now_ts = now.strftime('%Y-%m-%d %H:%M:%S.') + f"{now.microsecond // 1000:03d}"
    sort_base = int(now.timestamp() * 1000)

    for idx, (key, name, create_path, view_path, builder) in enumerate(MODELS):
        print(f"\n[{idx+1}/8] Processing: {key} ({name})")
        simple_model = builder()
        simple_json = json.dumps(simple_model, ensure_ascii=False)
        bpmn_xml = generate_bpmn_xml(key, name, simple_model)
        bpmn_bytes = bpmn_xml.encode('utf-8')
        simple_bytes = simple_json.encode('utf-8')

        # Check if model already exists and is deployed
        cur.execute("SELECT ID_, DEPLOYMENT_ID_, EDITOR_SOURCE_VALUE_ID_, EDITOR_SOURCE_EXTRA_VALUE_ID_ FROM `act_re_model` WHERE KEY_ = %s", (key,))
        existing = cur.fetchone()

        if existing and existing[1]:
            # Already deployed - create new version
            model_id = existing[0]
            old_deployment_id = existing[1]
            print(f"  Model already deployed (deployment={old_deployment_id}), creating new version")

            # Get current version
            cur.execute("SELECT MAX(VERSION_) FROM `act_re_procdef` WHERE KEY_ = %s", (key,))
            row = cur.fetchone()
            new_version = (row[0] or 0) + 1

            # Suspend old process definition
            cur.execute("UPDATE `act_re_procdef` SET SUSPENSION_STATE_ = 2 WHERE KEY_ = %s AND SUSPENSION_STATE_ = 1", (key,))

            # Create new deployment
            deploy_id = str(uuid.uuid4())
            cur.execute(
                "INSERT INTO `act_re_deployment` (ID_, NAME_, CATEGORY_, KEY_, TENANT_ID_, DEPLOY_TIME_) VALUES (%s,%s,'OA',%s,'1',%s)",
                (deploy_id, name, key, now_ts))

            # Insert BPMN XML as deployment resource
            bpmn_deploy_ba_id = str(uuid.uuid4())
            cur.execute(
                "INSERT INTO `act_ge_bytearray` (ID_, REV_, NAME_, DEPLOYMENT_ID_, BYTES_, GENERATED_) VALUES (%s,1,%s,%s,%s,0)",
                (bpmn_deploy_ba_id, f"{key}.bpmn", deploy_id, bpmn_bytes))

            # Insert BPMN XML as model source
            bpmn_source_ba_id = str(uuid.uuid4())
            cur.execute(
                "INSERT INTO `act_ge_bytearray` (ID_, REV_, NAME_, BYTES_, GENERATED_) VALUES (%s,1,'source',%s,0)",
                (bpmn_source_ba_id, bpmn_bytes))

            # Insert SIMPLE JSON as model source extra
            simple_ba_id = str(uuid.uuid4())
            cur.execute(
                "INSERT INTO `act_ge_bytearray` (ID_, REV_, NAME_, BYTES_, GENERATED_) VALUES (%s,1,'source_extra',%s,0)",
                (simple_ba_id, simple_bytes))

            # Create process definition
            short_deploy = deploy_id[:8]
            procdef_id = f"{key}:{new_version}:{short_deploy}"
            cur.execute(
                """INSERT INTO `act_re_procdef` (ID_, REV_, CATEGORY_, NAME_, KEY_, VERSION_, DEPLOYMENT_ID_,
                   RESOURCE_NAME_, HAS_START_FORM_KEY_, HAS_GRAPHICAL_NOTATION_, SUSPENSION_STATE_, TENANT_ID_, DERIVED_VERSION_)
                   VALUES (%s,1,'OA',%s,%s,%s,%s,%s,0,1,1,'1',0)""",
                (procdef_id, name, key, new_version, deploy_id, f"{key}.bpmn"))

            # Update model record
            meta_info = json.dumps({
                "icon": None, "description": "", "type": 20, "formType": 20, "formId": None,
                "formCustomCreatePath": create_path, "formCustomViewPath": view_path,
                "visible": True, "startUserIds": [], "startDeptIds": [], "managerUserIds": [1],
                "sort": sort_base + idx, "allowCancelRunningProcess": True, "allowWithdrawTask": True,
                "processIdRule": {"enable": False, "prefix": "", "infix": "", "postfix": "", "length": 5},
                "autoApprovalType": 0, "titleSetting": {"enable": False, "title": ""},
                "summarySetting": {"enable": False, "summary": []},
                "processBeforeTriggerSetting": None, "processAfterTriggerSetting": None,
                "taskBeforeTriggerSetting": None, "taskAfterTriggerSetting": None, "printTemplateSetting": None,
            }, ensure_ascii=False)
            cur.execute(
                """UPDATE `act_re_model` SET DEPLOYMENT_ID_=%s, EDITOR_SOURCE_VALUE_ID_=%s,
                   EDITOR_SOURCE_EXTRA_VALUE_ID_=%s, META_INFO_=%s, REV_=REV_+1, LAST_UPDATE_TIME_=%s
                   WHERE KEY_=%s""",
                (deploy_id, bpmn_source_ba_id, simple_ba_id, meta_info, now_ts, key))

            # Create bpm_process_definition_info
            cur.execute(
                """INSERT INTO `bpm_process_definition_info`
                   (process_definition_id, model_id, model_type, category, form_type,
                    form_custom_create_path, form_custom_view_path, simple_model, sort,
                    visible, start_user_ids, start_dept_ids, manager_user_ids,
                    allow_cancel_running_process, allow_withdraw_task, process_id_rule,
                    auto_approval_type, title_setting, summary_setting, deleted, tenant_id)
                   VALUES (%s,%s,20,'OA',20,%s,%s,%s,%s,b'1','','',%s,b'1',b'1',%s,0,%s,%s,0,0)""",
                (procdef_id, model_id, create_path, view_path, simple_json,
                 sort_base + idx, '1',
                 json.dumps({"enable": False, "prefix": "", "infix": "", "postfix": "", "length": 5}),
                 json.dumps({"enable": False, "title": ""}),
                 json.dumps({"enable": False, "summary": []})))

            print(f"  Deployed as version {new_version} (procdef_id={procdef_id})")

        elif existing:
            # Model exists but not deployed
            model_id = existing[0]
            print(f"  Model exists but not deployed, deploying...")

            deploy_id = str(uuid.uuid4())
            cur.execute(
                "INSERT INTO `act_re_deployment` (ID_, NAME_, CATEGORY_, KEY_, TENANT_ID_, DEPLOY_TIME_) VALUES (%s,%s,'OA',%s,'1',%s)",
                (deploy_id, name, key, now_ts))

            bpmn_deploy_ba_id = str(uuid.uuid4())
            cur.execute(
                "INSERT INTO `act_ge_bytearray` (ID_, REV_, NAME_, DEPLOYMENT_ID_, BYTES_, GENERATED_) VALUES (%s,1,%s,%s,%s,0)",
                (bpmn_deploy_ba_id, f"{key}.bpmn", deploy_id, bpmn_bytes))

            bpmn_source_ba_id = str(uuid.uuid4())
            cur.execute(
                "INSERT INTO `act_ge_bytearray` (ID_, REV_, NAME_, BYTES_, GENERATED_) VALUES (%s,1,'source',%s,0)",
                (bpmn_source_ba_id, bpmn_bytes))

            simple_ba_id = str(uuid.uuid4())
            cur.execute(
                "INSERT INTO `act_ge_bytearray` (ID_, REV_, NAME_, BYTES_, GENERATED_) VALUES (%s,1,'source_extra',%s,0)",
                (simple_ba_id, simple_bytes))

            short_deploy = deploy_id[:8]
            procdef_id = f"{key}:1:{short_deploy}"
            cur.execute(
                """INSERT INTO `act_re_procdef` (ID_, REV_, CATEGORY_, NAME_, KEY_, VERSION_, DEPLOYMENT_ID_,
                   RESOURCE_NAME_, HAS_START_FORM_KEY_, HAS_GRAPHICAL_NOTATION_, SUSPENSION_STATE_, TENANT_ID_, DERIVED_VERSION_)
                   VALUES (%s,1,'OA',%s,%s,1,%s,%s,0,1,1,'1',0)""",
                (procdef_id, name, key, deploy_id, f"{key}.bpmn"))

            meta_info = json.dumps({
                "icon": None, "description": "", "type": 20, "formType": 20, "formId": None,
                "formCustomCreatePath": create_path, "formCustomViewPath": view_path,
                "visible": True, "startUserIds": [], "startDeptIds": [], "managerUserIds": [1],
                "sort": sort_base + idx, "allowCancelRunningProcess": True, "allowWithdrawTask": True,
                "processIdRule": {"enable": False, "prefix": "", "infix": "", "postfix": "", "length": 5},
                "autoApprovalType": 0, "titleSetting": {"enable": False, "title": ""},
                "summarySetting": {"enable": False, "summary": []},
                "processBeforeTriggerSetting": None, "processAfterTriggerSetting": None,
                "taskBeforeTriggerSetting": None, "taskAfterTriggerSetting": None, "printTemplateSetting": None,
            }, ensure_ascii=False)
            cur.execute(
                """UPDATE `act_re_model` SET DEPLOYMENT_ID_=%s, EDITOR_SOURCE_VALUE_ID_=%s,
                   EDITOR_SOURCE_EXTRA_VALUE_ID_=%s, META_INFO_=%s, REV_=REV_+1, LAST_UPDATE_TIME_=%s
                   WHERE KEY_=%s""",
                (deploy_id, bpmn_source_ba_id, simple_ba_id, meta_info, now_ts, key))

            cur.execute(
                """INSERT INTO `bpm_process_definition_info`
                   (process_definition_id, model_id, model_type, category, form_type,
                    form_custom_create_path, form_custom_view_path, simple_model, sort,
                    visible, start_user_ids, start_dept_ids, manager_user_ids,
                    allow_cancel_running_process, allow_withdraw_task, process_id_rule,
                    auto_approval_type, title_setting, summary_setting, deleted, tenant_id)
                   VALUES (%s,%s,20,'OA',20,%s,%s,%s,%s,b'1','','',%s,b'1',b'1',%s,0,%s,%s,0,0)""",
                (procdef_id, model_id, create_path, view_path, simple_json,
                 sort_base + idx, '1',
                 json.dumps({"enable": False, "prefix": "", "infix": "", "postfix": "", "length": 5}),
                 json.dumps({"enable": False, "title": ""}),
                 json.dumps({"enable": False, "summary": []})))

            print(f"  Deployed as version 1 (procdef_id={procdef_id})")
        else:
            print(f"  WARNING: Model {key} not found in database, skipping!")

    conn.commit()
    print("\n" + "=" * 60)
    print("All models deployed successfully!")
    print("=" * 60)

    # Print summary
    cur.execute("""
        SELECT m.KEY_, m.NAME_, m.DEPLOYMENT_ID_ IS NOT NULL as deployed,
               p.VERSION_, p.SUSPENSION_STATE_
        FROM `act_re_model` m
        LEFT JOIN `act_re_procdef` p ON p.DEPLOYMENT_ID_ = m.DEPLOYMENT_ID_ AND p.KEY_ = m.KEY_
        WHERE m.KEY_ LIKE 'oa_%'
        ORDER BY m.KEY_
    """)
    print(f"\n{'Model Key':<35} {'Name':<20} {'Deployed':<10} {'Version':<8} {'Active':<8}")
    print("-" * 85)
    for row in cur.fetchall():
        print(f"{row[0]:<35} {row[1]:<20} {'Yes' if row[2] else 'No':<10} {row[3] or '-':<8} {'Yes' if row[4] == 1 else 'No':<8}")

    cur.close()
    conn.close()

if __name__ == '__main__':
    deploy_all()
