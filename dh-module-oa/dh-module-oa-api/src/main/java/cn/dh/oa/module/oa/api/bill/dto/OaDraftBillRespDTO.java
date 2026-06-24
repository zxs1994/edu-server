package cn.dh.oa.module.oa.api.bill.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * OA 未提交草稿单据（无流程实例）
 */
@Data
public class OaDraftBillRespDTO {

    private Long billId;
    private String billCode;
    private String processDefinitionKey;
    private String summary;
    private LocalDateTime createTime;
    private Long companyId;
    private String companyName;
    private Long deptId;
    private String deptName;

}
