package cn.dh.oa.module.oa.controller.admin.incoming.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.dh.oa.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.dh.oa.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 收文办理单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class IncomingDocumentBillPageReqVO extends PageParam {

    @Schema(description = "单据编号")
    private String billCode;

    @Schema(description = "单据状态")
    private Integer processStatus;

    @Schema(description = "公文标题")
    private String docTitle;

    @Schema(description = "来文字号")
    private String docNumber;

    @Schema(description = "收文类型：1上级文件 2平级文件 3下级文件 4群众来信 5其他")
    private Integer docType;

    @Schema(description = "紧急程度：0普通 1紧急 2特急")
    private Integer urgencyLevel;

    @Schema(description = "办理状态：0待办理 1办理中 2已办结")
    private Integer handlingStatus;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
