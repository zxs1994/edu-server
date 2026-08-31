package cn.dh.edu.module.edu.api.teacher;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.module.edu.api.teacher.dto.TeacherUpdateReqDTO;
import cn.dh.edu.module.edu.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import static cn.dh.edu.module.edu.api.teacher.TeacherApi.PREFIX;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 教培档案")
public interface TeacherApi {

    String PREFIX = ApiConstants.PREFIX + "/teacher";

    @PutMapping(PREFIX + "/update-by-user-id")
    @Operation(summary = "根据用户ID更新教培档案")
    @Parameter(name = "userId", description = "用户编号", example = "1", required = true)
    CommonResult<Boolean> updateTeacherByUserId(@RequestParam("userId") Long userId,
                                                @RequestBody TeacherUpdateReqDTO updateReqDTO);

    @DeleteMapping(PREFIX + "/delete-by-user-id")
    @Operation(summary = "根据用户ID删除教培档案")
    @Parameter(name = "userId", description = "用户编号", example = "1", required = true)
    CommonResult<Boolean> deleteTeacherByUserId(@RequestParam("userId") Long userId);

}
