package cn.dh.edu.module.edu.service.bill;

import cn.dh.edu.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import cn.dh.edu.module.edu.api.bill.EduDraftBillApi;
import cn.dh.edu.module.edu.api.bill.dto.EduDraftBillQueryDTO;
import cn.dh.edu.module.edu.api.bill.dto.EduDraftBillRespDTO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityPaymentRequestDO;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityPaymentRequestMapper;
import cn.dh.edu.module.edu.enums.EduBillTypeEnum;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 查询 EDU 未提交草稿单据（专项活动、付款申请）
 */
@Service
public class EduDraftBillService implements EduDraftBillApi {

    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private ActivityPaymentRequestMapper activityPaymentRequestMapper;

    @Override
    public List<EduDraftBillRespDTO> listMyDraftBills(Long userId, EduDraftBillQueryDTO query) {
        if (userId == null) {
            return List.of();
        }
        EduDraftBillQueryDTO safeQuery = query != null ? query : new EduDraftBillQueryDTO();
        List<EduDraftBillRespDTO> result = new ArrayList<>();
        String creator = String.valueOf(userId);
        Integer notStart = BpmProcessInstanceStatusEnum.NOT_START.getStatus();

        if (shouldQuery(EduBillTypeEnum.ACTIVITY.getProcessDefinitionKey(), safeQuery)) {
            activityMapper.selectList(draftWrapper(ActivityDO.class, creator, notStart, safeQuery, true))
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            EduBillTypeEnum.ACTIVITY.getProcessDefinitionKey(), bill.getName(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(EduBillTypeEnum.ACTIVITY_PAYMENT_REQUEST.getProcessDefinitionKey(), safeQuery)) {
            // 付款申请表无公司/部门字段：带公司或部门筛选时不查该类草稿，避免误展示
            if (safeQuery.getCompanyId() == null && safeQuery.getDeptId() == null) {
                activityPaymentRequestMapper.selectList(
                                draftWrapper(ActivityPaymentRequestDO.class, creator, notStart, safeQuery, false))
                        .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                                EduBillTypeEnum.ACTIVITY_PAYMENT_REQUEST.getProcessDefinitionKey(), bill.getTitle(),
                                bill.getCreateTime(), null, null, null, null)));
            }
        }

        result.sort(Comparator.comparing(EduDraftBillRespDTO::getCreateTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    private boolean shouldQuery(String processDefinitionKey, EduDraftBillQueryDTO query) {
        if (StrUtil.isNotBlank(query.getProcessDefinitionKey())) {
            return processDefinitionKey.equals(query.getProcessDefinitionKey());
        }
        if (CollUtil.isNotEmpty(query.getProcessDefinitionKeys())) {
            return query.getProcessDefinitionKeys().contains(processDefinitionKey);
        }
        return true;
    }

    private <T> LambdaQueryWrapper<T> draftWrapper(Class<T> clazz, String creator, Integer notStart,
                                                   EduDraftBillQueryDTO query, boolean hasOrgFields) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>(clazz);
        wrapper.apply("creator = {0}", creator)
                .apply("process_status IN ({0}, 0)", notStart)
                .and(w -> w.apply("process_instance_id IS NULL").or().apply("process_instance_id = ''"));
        if (StrUtil.isNotBlank(query.getBillCode())) {
            wrapper.apply("bill_code LIKE {0}", "%" + query.getBillCode() + "%");
        }
        if (hasOrgFields) {
            if (query.getCompanyId() != null) {
                wrapper.apply("company_id = {0}", query.getCompanyId());
            }
            if (query.getDeptId() != null) {
                wrapper.apply("dept_id = {0}", query.getDeptId());
            }
        }
        if (query.getCreateTimeStart() != null) {
            wrapper.apply("create_time >= {0}", query.getCreateTimeStart());
        }
        if (query.getCreateTimeEnd() != null) {
            wrapper.apply("create_time <= {0}", query.getCreateTimeEnd());
        }
        return wrapper;
    }

    private EduDraftBillRespDTO toDto(Long billId, String billCode, String processDefinitionKey, String summary,
                                      java.time.LocalDateTime createTime, Long companyId, String companyName,
                                      Long deptId, String deptName) {
        EduDraftBillRespDTO dto = new EduDraftBillRespDTO();
        dto.setBillId(billId);
        dto.setBillCode(billCode);
        dto.setProcessDefinitionKey(processDefinitionKey);
        dto.setSummary(summary);
        dto.setCreateTime(createTime);
        dto.setCompanyId(companyId);
        dto.setCompanyName(companyName);
        dto.setDeptId(deptId);
        dto.setDeptName(deptName);
        return dto;
    }

}
