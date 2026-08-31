package cn.dh.edu.module.hrm.enums;

import cn.dh.edu.framework.common.enums.BillTypeEnum;

/**
 * HRM 单据类型枚举
 *
 * @author 鼎衡
 */
public enum HrmBillTypeEnum implements BillTypeEnum {
    /**
     * 员工入职申请单
     */
    HRM_EMPLOYEE_ENTRY_BILL("201", "员工入职申请单", "hr_employee_entry_bill"),

    /**
     * 员工转正申请单
     */
    HRM_EMPLOYEE_REGULAR_BILL("202", "员工转正申请单", "hr_employee_regular_bill"),

    /**
     * 人事调动申请单
     */
    HRM_EMPLOYEE_TRANSFER_BILL("203", "人事调动申请单", "hr_employee_transfer_bill"),

    /**
     * 员工离职申请单
     */
    HRM_EMPLOYEE_RESIGNATION_BILL("204", "员工离职申请单", "hr_employee_resignation_bill");

    /**
     * 单据类型代码
     */
    private final String code;

    /**
     * 单据类型名称
     */
    private final String name;

    /**
     * 流程定义key
     */
    private final String processDefinitionKey;

    /**
     * 构造方法
     *
     * @param code 单据类型代码
     * @param name 单据类型名称
     * @param processDefinitionKey 流程定义key
     */
    HrmBillTypeEnum(String code, String name, String processDefinitionKey) {
        this.code = code;
        this.name = name;
        this.processDefinitionKey = processDefinitionKey;
    }

    @Override
    public String getTypeCode() {
        return code;
    }

    @Override
    public String getTypeName() {
        return name;
    }

    @Override
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public static HrmBillTypeEnum getByProcessDefinitionKey(String processDefinitionKey) {
        if (processDefinitionKey == null) {
            return null;
        }
        for (HrmBillTypeEnum value : values()) {
            if (processDefinitionKey.equals(value.processDefinitionKey)) {
                return value;
            }
        }
        return null;
    }

}


