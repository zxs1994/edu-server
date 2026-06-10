package cn.dh.oa.module.oa.controller.admin.contract.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.dh.oa.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.dh.oa.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 合同审批单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ContractBillPageReqVO extends PageParam {

    @Schema(description = "单据编号", example = "HT202412010001")
    private String billCode;

    @Schema(description = "单据状态", example = "1")
    private Integer processStatus;

    @Schema(description = "合同标题", example = "采购合同")
    private String contractTitle;

    @Schema(description = "合同类型", example = "1")
    private Integer contractType;

    @Schema(description = "合同对方", example = "ABC公司")
    private String contractParty;

    @Schema(description = "是否重大合同", example = "0")
    private Integer isMajor;

    @Schema(description = "公司ID", example = "1")
    private Long companyId;

    @Schema(description = "公司名称", example = "鼎衡科技")
    private String companyName;

    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    @Schema(description = "部门名称", example = "技术部")
    private String deptName;

    @Schema(description = "创建人（用户ID）", example = "1")
    private String creator;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
