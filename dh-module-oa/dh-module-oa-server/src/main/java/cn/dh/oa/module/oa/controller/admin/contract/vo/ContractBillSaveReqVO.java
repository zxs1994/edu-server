package cn.dh.oa.module.oa.controller.admin.contract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import cn.dh.oa.common.server.attachment.controller.vo.AttachmentSaveReqVO;

@Schema(description = "管理后台 - 合同审批单新增/修改 Request VO")
@Data
public class ContractBillSaveReqVO {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "单据编号", example = "HT202412010001")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "1")
    private Integer processStatus;

    @Schema(description = "合同标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "采购合同")
    @NotEmpty(message = "合同标题不能为空")
    private String contractTitle;

    @Schema(description = "合同类型（1采购 2销售 3服务 4合作 5其他）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "合同类型不能为空")
    private Integer contractType;

    @Schema(description = "合同对方", requiredMode = Schema.RequiredMode.REQUIRED, example = "ABC公司")
    @NotEmpty(message = "合同对方不能为空")
    private String contractParty;

    @Schema(description = "合同金额", example = "100000.00")
    private BigDecimal contractAmount;

    @Schema(description = "合同开始日期")
    private LocalDate contractStartDate;

    @Schema(description = "合同结束日期")
    private LocalDate contractEndDate;

    @Schema(description = "合同内容")
    private String contractContent;

    @Schema(description = "是否重大合同（0否 1是）", example = "0")
    private Integer isMajor;

    @Schema(description = "重大合同备注")
    private String majorRemark;

    @Schema(description = "申请事由", requiredMode = Schema.RequiredMode.REQUIRED, example = "签订采购合同")
    @NotEmpty(message = "申请事由不能为空")
    private String cause;

    @Schema(description = "申请人姓名", example = "张三")
    private String creatorName;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "公司ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "公司ID不能为空")
    private Long companyId;

    @Schema(description = "公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "鼎衡科技")
    @NotEmpty(message = "公司名称不能为空")
    private String companyName;

    @Schema(description = "部门ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "部门ID不能为空")
    private Long deptId;

    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "技术部")
    @NotEmpty(message = "部门名称不能为空")
    private String deptName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;

}
