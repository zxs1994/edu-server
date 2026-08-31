package cn.dh.edu.module.edu.controller.admin.student;

import cn.dh.edu.framework.apilog.core.annotation.ApiAccessLog;
import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.framework.common.pojo.PageParam;
import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.excel.core.util.ExcelUtils;
import cn.dh.edu.module.edu.controller.admin.student.vo.StudentCreateRespVO;
import cn.dh.edu.module.edu.controller.admin.student.vo.StudentPageReqVO;
import cn.dh.edu.module.edu.controller.admin.student.vo.StudentRespVO;
import cn.dh.edu.module.edu.controller.admin.student.vo.StudentSaveReqVO;
import cn.dh.edu.module.edu.service.student.StudentService;
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

@Tag(name = "管理后台 - 学生档案")
@RestController
@RequestMapping("/edu/student")
@Validated
public class StudentController {

    @Resource
    private StudentService studentService;

    @PostMapping("/create")
    @Operation(summary = "创建学生档案")
    @PreAuthorize("@ss.hasPermission('edu:student:create')")
    public CommonResult<StudentCreateRespVO> createStudent(@Valid @RequestBody StudentSaveReqVO createReqVO) {
        return success(studentService.createStudent(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新学生档案")
    @PreAuthorize("@ss.hasPermission('edu:student:update')")
    public CommonResult<Boolean> updateStudent(@Valid @RequestBody StudentSaveReqVO updateReqVO) {
        studentService.updateStudent(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除学生档案")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('edu:student:delete')")
    public CommonResult<Boolean> deleteStudent(@RequestParam("id") Long id) {
        studentService.deleteStudent(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除学生档案")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "[1,2,3]")
    @PreAuthorize("@ss.hasPermission('edu:student:delete')")
    public CommonResult<Boolean> deleteStudentList(@RequestParam("ids") List<Long> ids) {
        studentService.deleteStudentList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得学生档案")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('edu:student:query')")
    public CommonResult<StudentRespVO> getStudent(@RequestParam("id") Long id) {
        return success(studentService.getStudent(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得学生档案分页")
    @PreAuthorize("@ss.hasPermission('edu:student:query')")
    public CommonResult<PageResult<StudentRespVO>> getStudentPage(@Valid StudentPageReqVO pageReqVO) {
        return success(studentService.getStudentPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出学生档案 Excel")
    @PreAuthorize("@ss.hasPermission('edu:student:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportStudentExcel(@Valid StudentPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<StudentRespVO> list = studentService.getStudentPage(pageReqVO).getList();
        ExcelUtils.write(response, "学生档案.xls", "数据", StudentRespVO.class, list);
    }

}
