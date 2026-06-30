package cn.dh.oa.module.oa.controller.admin.correction.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "业务单据 - 会长纠错历史")
@Data
public class BillCorrectionHistoryRespVO {

    @Schema(description = "冻结状态 0正常 1已冻结")
    private Integer freezeStatus;

    @Schema(description = "纠错状态")
    private Integer correctionStatus;

    @Schema(description = "纠错历史（按版本升序）")
    private List<BillCorrectionHistoryItemVO> items;

}
