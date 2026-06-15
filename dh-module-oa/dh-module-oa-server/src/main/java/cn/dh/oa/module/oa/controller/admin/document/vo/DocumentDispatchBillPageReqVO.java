package cn.dh.oa.module.oa.controller.admin.document.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.dh.oa.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.dh.oa.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 公文发文单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DocumentDispatchBillPageReqVO extends PageParam {

    @Schema(description = "单据编号", example = "GW202412010001")
    private String billCode;

    @Schema(description = "单据状态", example = "1")
    private Integer processStatus;

    @Schema(description = "公文标题", example = "关于XXX的通知")
    private String docTitle;

    @Schema(description = "公文编号", example = "DH-2024-001")
    private String docNumber;

    @Schema(description = "密级（0公开 1内部 2机密 3绝密）", example = "0")
    private Integer secrecyLevel;

    @Schema(description = "紧急程度", example = "0")
    private Integer urgencyLevel;

    @Schema(description = "公司ID", example = "1")
    private Long companyId;

    @Schema(description = "公司名称", example = "鼎衡科技")
    private String companyName;

    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    @Schema(description = "部门名称", example = "行政部")
    private String deptName;

    @Schema(description = "创建人（用户ID）", example = "1")
    private String creator;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
