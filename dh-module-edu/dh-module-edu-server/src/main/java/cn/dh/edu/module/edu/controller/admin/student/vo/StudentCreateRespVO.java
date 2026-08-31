package cn.dh.edu.module.edu.controller.admin.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 学生档案创建 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCreateRespVO {

    @Schema(description = "学生档案编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "登录用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "20240001")
    private String username;

    @Schema(description = "初始密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String initPassword;

}
