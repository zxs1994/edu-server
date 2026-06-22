package cn.dh.oa.module.oa.controller.admin.travel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @Schema(description = "出差事由", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "出差事由不能为空")
    private String cause;

    @Schema(description = "出差开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime travelStartDate;

    @Schema(description = "出差结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime travelEndDate;

    @Schema(description = "出差天数（支持1位小数）", example = "3.5")
    private BigDecimal travelDays;

    @Schema(description = "同行人")
    private String companion;

    @Schema(description = "预计费用")
    private BigDecimal estimatedCost;

    @Schema(description = "报销状态（0未报销 1已报销）")
    private Integer reimbursementStatus;

    @Schema(description = "申请类型（1国内差旅 2出境差旅）")
    private Integer travelType;

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

    @Schema(description = "行程明细列表")
    private List<TravelItinerarySaveReqVO> itineraries;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;

}
