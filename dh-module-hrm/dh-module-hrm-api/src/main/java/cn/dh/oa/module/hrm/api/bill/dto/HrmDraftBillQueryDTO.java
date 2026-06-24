package cn.dh.oa.module.hrm.api.bill.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * HRM 草稿单据查询条件
 */
@Data
public class HrmDraftBillQueryDTO {

    private String billCode;
    private String processDefinitionKey;
    private Set<String> processDefinitionKeys;
    private Long companyId;
    private Long deptId;
    private LocalDateTime createTimeStart;
    private LocalDateTime createTimeEnd;

}
