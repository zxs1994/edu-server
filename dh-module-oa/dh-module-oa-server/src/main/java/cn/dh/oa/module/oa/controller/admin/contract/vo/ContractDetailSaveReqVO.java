package cn.dh.oa.module.oa.controller.admin.contract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 合同明细新增/修改 Request VO")
@Data
public class ContractDetailSaveReqVO {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "合同审批单ID", example = "1")
    private Long billId;

    @Schema(description = "项目/产品名称")
    private String productName;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "数量", example = "1.0000")
    private BigDecimal quantity;

    @Schema(description = "单价", example = "0.00")
    private BigDecimal unitPrice;

    @Schema(description = "金额", example = "0.00")
    private BigDecimal amount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

}
