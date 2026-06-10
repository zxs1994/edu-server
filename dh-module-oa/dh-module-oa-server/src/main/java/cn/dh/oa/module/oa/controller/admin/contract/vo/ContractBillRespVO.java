package cn.dh.oa.module.oa.controller.admin.contract.vo;

import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import java.util.List;

@Schema(description = "管理后台 - 合同审批单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ContractBillRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1882")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "单据编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单据编号")
    private String billCode;

    @Schema(description = "流程实例编号")
    @ExcelProperty("流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "2")
    @ExcelProperty("单据状态")
    private Integer processStatus;

    @Schema(description = "合同标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "采购合同")
    @ExcelProperty("合同标题")
    private String contractTitle;

    @Schema(description = "合同类型（1采购 2销售 3服务 4合作 5其他）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("合同类型")
    private Integer contractType;

    @Schema(description = "合同对方", requiredMode = Schema.RequiredMode.REQUIRED, example = "ABC公司")
    @ExcelProperty("合同对方")
    private String contractParty;

    @Schema(description = "合同金额", example = "100000.00")
    @ExcelProperty("合同金额")
    private BigDecimal contractAmount;

    @Schema(description = "合同开始日期")
    @ExcelProperty("合同开始日期")
    private LocalDate contractStartDate;

    @Schema(description = "合同结束日期")
    @ExcelProperty("合同结束日期")
    private LocalDate contractEndDate;

    @Schema(description = "合同内容")
    @ExcelProperty("合同内容")
    private String contractContent;

    @Schema(description = "是否重大合同（0否 1是）", example = "0")
    @ExcelProperty("是否重大合同")
    private Integer isMajor;

    @Schema(description = "重大合同备注")
    @ExcelProperty("重大合同备注")
    private String majorRemark;

    @Schema(description = "申请事由", requiredMode = Schema.RequiredMode.REQUIRED, example = "签订采购合同")
    @ExcelProperty("申请事由")
    private String cause;

    @Schema(description = "创建者", example = "1")
    @ExcelProperty("创建者")
    private String creator;

    @Schema(description = "创建者姓名", example = "张三")
    @ExcelProperty("创建者姓名")
    private String creatorName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "部门ID", example = "1")
    @ExcelProperty("部门ID")
    private Long deptId;

    @Schema(description = "部门名称", example = "技术部")
    @ExcelProperty("部门名称")
    private String deptName;

    @Schema(description = "公司ID", example = "1")
    @ExcelProperty("公司ID")
    private Long companyId;

    @Schema(description = "公司名称", example = "鼎衡科技")
    @ExcelProperty("公司名称")
    private String companyName;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;

}
