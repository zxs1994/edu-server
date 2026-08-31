package cn.dh.edu.module.edu.controller.admin.teacher.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 教培档案创建 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCreateRespVO {

    @Schema(description = "教培档案编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "登录用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "teacher01")
    private String username;

    @Schema(description = "初始密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String initPassword;

}
