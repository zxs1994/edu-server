package cn.dh.oa.module.oa.controller.admin.reception.vo;

import cn.dh.oa.common.server.attachment.controller.vo.AttachmentSaveReqVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "管理后台 - 接待申请单 Save VO")
@Data
public class ReceptionApplyBillSaveReqVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "单据编号")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态")
    private Integer processStatus;

    @Schema(description = "申请事由", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "申请事由不能为空")
    private String cause;

    @Schema(description = "就餐时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "就餐时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate diningTime;

    @Schema(description = "就餐标准", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "就餐标准不能为空")
    private String diningStandard;

    @Schema(description = "来宾人数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "来宾人数不能为空")
    private Integer guestCount;

    @Schema(description = "陪同人数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "陪同人数不能为空")
    private Integer accompanyCount;

    @Schema(description = "预估费用", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "预估费用不能为空")
    private BigDecimal estimatedCost;

    @Schema(description = "申请人姓名")
    private String creatorName;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "公司ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "公司ID不能为空")
    private Long companyId;

    @Schema(description = "公司名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "公司名称不能为空")
    private String companyName;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;

}
