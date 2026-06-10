package cn.dh.oa.module.oa.controller.admin.project;

import cn.dh.oa.framework.apilog.core.annotation.ApiAccessLog;
import cn.dh.oa.framework.common.pojo.CommonResult;
import cn.dh.oa.framework.common.pojo.PageParam;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import cn.dh.oa.framework.excel.core.util.ExcelUtils;
import cn.dh.oa.module.oa.controller.admin.project.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.project.ProjectInitiationBillDO;
import cn.dh.oa.module.oa.service.project.ProjectInitiationBillService;
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

import static cn.dh.oa.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.dh.oa.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 项目立项单")
@RestController
@RequestMapping("/oa/project-initiation-bill")
@Validated
public class ProjectInitiationBillController {

    @Resource
    private ProjectInitiationBillService projectInitiationBillService;

    @PostMapping("/create")
    @Operation(summary = "创建项目立项单")
    @PreAuthorize("@ss.hasPermission('oa:project-initiation-bill:create')")
    public CommonResult<Long> createProjectInitiationBill(@Valid @RequestBody ProjectInitiationBillSaveReqVO createReqVO) {
        return success(projectInitiationBillService.createProjectInitiationBill(createReqVO));
    }

    @PostMapping("/save")
    @Operation(summary = "保存项目立项单")
    @PreAuthorize("@ss.hasPermission('oa:project-initiation-bill:create')")
    public CommonResult<Long> saveProjectInitiationBill(@Valid @RequestBody ProjectInitiationBillSaveReqVO saveReqVO) {
        return success(projectInitiationBillService.saveProjectInitiationBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交项目立项单")
    @PreAuthorize("@ss.hasPermission('oa:project-initiation-bill:create')")
    public CommonResult<Long> submitProjectInitiationBill(@Valid @RequestBody ProjectInitiationBillSaveReqVO submitReqVO) {
        return success(projectInitiationBillService.submitProjectInitiationBill(submitReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新项目立项单")
    @PreAuthorize("@ss.hasPermission('oa:project-initiation-bill:update')")
    public CommonResult<Boolean> updateProjectInitiationBill(@Valid @RequestBody ProjectInitiationBillSaveReqVO updateReqVO) {
        projectInitiationBillService.updateProjectInitiationBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除项目立项单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:project-initiation-bill:delete')")
    public CommonResult<Boolean> deleteProjectInitiationBill(@RequestParam("id") Long id) {
        projectInitiationBillService.deleteProjectInitiationBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除项目立项单")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('oa:project-initiation-bill:delete')")
    public CommonResult<Boolean> deleteProjectInitiationBillList(@RequestParam("ids") List<Long> ids) {
        projectInitiationBillService.deleteProjectInitiationBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得项目立项单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:project-initiation-bill:query')")
    public CommonResult<ProjectInitiationBillRespVO> getProjectInitiationBill(@RequestParam("id") Long id) {
        ProjectInitiationBillRespVO respVO = projectInitiationBillService.getProjectInitiationBillInfo(id);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得项目立项单分页")
    @PreAuthorize("@ss.hasPermission('oa:project-initiation-bill:query')")
    public CommonResult<PageResult<ProjectInitiationBillRespVO>> getProjectInitiationBillPage(@Valid ProjectInitiationBillPageReqVO pageReqVO) {
        PageResult<ProjectInitiationBillDO> pageResult = projectInitiationBillService.getProjectInitiationBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ProjectInitiationBillRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出项目立项单 Excel")
    @PreAuthorize("@ss.hasPermission('oa:project-initiation-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportProjectInitiationBillExcel(@Valid ProjectInitiationBillPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProjectInitiationBillDO> list = projectInitiationBillService.getProjectInitiationBillPage(pageReqVO).getList();
        ExcelUtils.write(response, "项目立项单.xls", "数据", ProjectInitiationBillRespVO.class,
                        BeanUtils.toBean(list, ProjectInitiationBillRespVO.class));
    }

}
