package cn.dh.oa.module.oa.api.correction.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会长纠错待重提 - 待办列表筛选（与 BPM 待办查询字段对齐）
 */
@Data
public class OaCorrectionResubmitTodoQueryDTO {

    /** 单据类型（流程定义 Key） */
    private String billType;

    /** 单据编号 */
    private String billCode;

    /** 接收时间起（对应纠错撤销时间） */
    private LocalDateTime receiveTimeStart;

    /** 接收时间止 */
    private LocalDateTime receiveTimeEnd;

    private Long companyId;

    private Long deptId;

}
