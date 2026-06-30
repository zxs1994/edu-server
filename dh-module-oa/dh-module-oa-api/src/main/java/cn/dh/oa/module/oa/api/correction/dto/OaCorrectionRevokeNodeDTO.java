package cn.dh.oa.module.oa.api.correction.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会长纠错 - 审批详情虚拟「流程撤销」节点
 */
@Data
public class OaCorrectionRevokeNodeDTO {

    private Long revokeUserId;

    private String revokeUserName;

    private String correctionReason;

    private LocalDateTime revokeTime;

}
