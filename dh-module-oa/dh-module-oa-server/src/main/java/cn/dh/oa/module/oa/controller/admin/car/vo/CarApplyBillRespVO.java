package cn.dh.oa.module.oa.controller.admin.car.vo;

import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;

@Schema(description = "管理后台 - 用车申请单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CarApplyBillRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1882")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "单据编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单据编号")
    private String billCode;

    @Schema(description = "流程实例编号", example = "16629")
    @ExcelProperty("流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "2")
    @ExcelProperty("单据状态")
    private Integer processStatus;

    @Schema(description = "车辆ID(逗号分隔)", example = "1,2,3")
    @ExcelProperty("车辆ID")
    private String carId;

    @Schema(description = "车牌号", example = "3524")
    @ExcelProperty("车牌号")
    private String carNo;

    @Schema(description = "出车时间")
    @ExcelProperty("出车时间")
    private LocalDateTime goTime;

    @Schema(description = "回车时间")
    @ExcelProperty("回车时间")
    private LocalDateTime returnTime;

    @Schema(description = "出车地点")
    @ExcelProperty("出车地点")
    private String goArea;

    @Schema(description = "回车地点")
    @ExcelProperty("回车地点")
    private String returnArea;

    @Schema(description = "用车事由")
    @ExcelProperty("用车事由")
    private String cause;

    @Schema(description = "申请人")
    @ExcelProperty("申请人")
    private String applyer;

    @Schema(description = "随行人")
    @ExcelProperty("随行人")
    private String passenger;

    @Schema(description = "创建者", example = "1")
    @ExcelProperty("创建者")
    private String creator;

    @Schema(description = "创建者姓名", example = "芋艿")
    @ExcelProperty("创建者姓名")
    private String creatorName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "部门ID", example = "12287")
    @ExcelProperty("部门ID")
    private Long deptId;

    @Schema(description = "部门名称", example = "张三")
    @ExcelProperty("部门名称")
    private String deptName;

    @Schema(description = "还车状态", example = "0")
    @ExcelProperty("还车状态")
    private Integer returnStatus;

    @Schema(description = "备注", example = "紧急用车")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;

}