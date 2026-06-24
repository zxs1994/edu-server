package cn.dh.oa.module.bpm.controller.admin.draft.vo;

import cn.dh.oa.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.dh.oa.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 草稿箱分页 Request VO")
@Data
public class BpmDraftBillPageReqVO extends PageParam {

    @Schema(description = "流程分类", example = "OA")
    private String category;

    @Schema(description = "单据类型（流程定义 key）", example = "oa_travel_apply_bill")
    private String billType;

    @Schema(description = "单据编号", example = "CC20250101001")
    private String billCode;

    @Schema(description = "所属公司ID", example = "1")
    private Long companyId;

    @Schema(description = "所属部门ID", example = "1")
    private Long deptId;

    @Schema(description = "保存时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
