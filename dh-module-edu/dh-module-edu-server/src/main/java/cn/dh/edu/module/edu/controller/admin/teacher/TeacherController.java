package cn.dh.edu.module.edu.controller.admin.teacher;

import cn.dh.edu.framework.apilog.core.annotation.ApiAccessLog;
import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.framework.common.pojo.PageParam;
import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.excel.core.util.ExcelUtils;
import cn.dh.edu.module.edu.controller.admin.teacher.vo.TeacherCreateRespVO;
import cn.dh.edu.module.edu.controller.admin.teacher.vo.TeacherPageReqVO;
import cn.dh.edu.module.edu.controller.admin.teacher.vo.TeacherRespVO;
import cn.dh.edu.module.edu.controller.admin.teacher.vo.TeacherSaveReqVO;
import cn.dh.edu.module.edu.service.teacher.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.dh.edu.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.dh.edu.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 教培档案")
@RestController
@RequestMapping("/edu/teacher")
@Validated
public class TeacherController {

    @Resource
    private TeacherService teacherService;

    @PostMapping("/create")
    @Operation(summary = "创建教培档案")
    @PreAuthorize("@ss.hasPermission('edu:teacher:create')")
    public CommonResult<TeacherCreateRespVO> createTeacher(@Valid @RequestBody TeacherSaveReqVO createReqVO) {
        return success(teacherService.createTeacher(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新教培档案")
    @PreAuthorize("@ss.hasPermission('edu:teacher:update')")
    public CommonResult<Boolean> updateTeacher(@Valid @RequestBody TeacherSaveReqVO updateReqVO) {
        teacherService.updateTeacher(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除教培档案")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('edu:teacher:delete')")
    public CommonResult<Boolean> deleteTeacher(@RequestParam("id") Long id) {
        teacherService.deleteTeacher(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除教培档案")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('edu:teacher:delete')")
    public CommonResult<Boolean> deleteTeacherList(@RequestParam("ids") List<Long> ids) {
        teacherService.deleteTeacherList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得教培档案")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('edu:teacher:query')")
    public CommonResult<TeacherRespVO> getTeacher(@RequestParam("id") Long id) {
        return success(teacherService.getTeacher(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得教培档案分页")
    @PreAuthorize("@ss.hasPermission('edu:teacher:query')")
    public CommonResult<PageResult<TeacherRespVO>> getTeacherPage(@Valid TeacherPageReqVO pageReqVO) {
        return success(teacherService.getTeacherPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出教培档案 Excel")
    @PreAuthorize("@ss.hasPermission('edu:teacher:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTeacherExcel(@Valid TeacherPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TeacherRespVO> list = teacherService.getTeacherPage(pageReqVO).getList();
        ExcelUtils.write(response, "教培档案.xls", "数据", TeacherRespVO.class, list);
    }

}
