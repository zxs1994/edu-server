package cn.dh.oa.module.oa.service.correction.dto;

import lombok.Data;

/**
 * 纠错原单据快照
 */
@Data
public class BillCorrectionSourceDTO {

    private String billType;

    private Long billId;

    private String billCode;

    private String billTitle;

    private String processInstanceId;

    private Integer processStatus;

    private Long creatorUserId;

    private String creatorName;

    private Long companyId;

    private String companyName;

    private Long deptId;

    private String deptName;

}
