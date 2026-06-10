package cn.dh.oa.module.oa.controller.admin.travel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import cn.dh.oa.common.server.attachment.controller.vo.AttachmentSaveReqVO;

@Schema(description = "管理后台 - 差旅申请单新增/修改 Request VO")
@Data
public class TravelApplyBillSaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "单据编号")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态")
    private Integer processStatus;

    // ========== 业务字段 ==========

    @Schema(description = "目的地", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "目的地不能为空")
    private String destination;

    @Schema(description = "出差开始时间")
    private LocalDateTime travelStartDate;

    @Schema(description = "出差结束时间")
    private LocalDateTime travelEndDate;

    @Schema(description = "出差天数")
    private Integer travelDays;

    @Schema(description = "交通方式：1火车 2飞机 3自驾 4公务用车 5其他")
    private Integer transportType;

    @Schema(description = "住宿类型：1酒店 2招待所 3其他")
    private Integer accommodationType;

    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;

    @Schema(description = "预算明细")
    private String budgetDetail;

    @Schema(description = "出差人员")
    private String travelMembers;

    @Schema(description = "是否出国：0否 1是")
    private Integer isOverseas;

    @Schema(description = "出国备注")
    private String overseasRemark;

    @Schema(description = "事由", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "事由不能为空")
    private String cause;

    // ========== 公共字段 ==========

    @Schema(description = "创建者姓名")
    private String creatorName;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "公司编号")
    private Long companyId;

    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "部门编号")
    private Long deptId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;

}
