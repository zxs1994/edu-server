package cn.dh.edu.module.edu.api.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "RPC 服务 - 学生更新 Request DTO")
@Data
public class StudentUpdateReqDTO {

    @Schema(description = "用户账号", example = "20240001")
    private String username;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "手机号", example = "13800138000")
    private String mobile;

    @Schema(description = "性别（1:男 2:女）", example = "1")
    private Integer sex;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
