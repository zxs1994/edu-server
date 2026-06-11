package cn.dh.oa.module.oa.controller.admin.contract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 合同明细 Response VO")
@Data
public class ContractDetailRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
