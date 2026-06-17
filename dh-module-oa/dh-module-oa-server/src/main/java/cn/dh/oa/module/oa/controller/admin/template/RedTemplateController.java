package cn.dh.oa.module.oa.controller.admin.template;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.dh.oa.framework.common.pojo.PageParam;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.pojo.CommonResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import static cn.dh.oa.framework.common.pojo.CommonResult.success;

import cn.dh.oa.framework.excel.core.util.ExcelUtils;

import cn.dh.oa.framework.apilog.core.annotation.ApiAccessLog;
import static cn.dh.oa.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.dh.oa.module.oa.controller.admin.template.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.template.RedTemplateDO;
import cn.dh.oa.module.oa.service.template.RedTemplateService;

@Tag(name = "OA协同办公 - 套红模板")
@RestController
@RequestMapping("/oa/red-template")
@Validated
public class RedTemplateController {

    @Resource
    private RedTemplateService redTemplateService;

    @PostMapping("/create")
    @Operation(summary = "创建套红模板")
    @PreAuthorize("@ss.hasPermission('oa:red-template:create')")
    public CommonResult<Long> createRedTemplate(@Valid @RequestBody RedTemplateSaveReqVO createReqVO) {
        return success(redTemplateService.createRedTemplate(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新套红模板")
    @PreAuthorize("@ss.hasPermission('oa:red-template:update')")
    public CommonResult<Boolean> updateRedTemplate(@Valid @RequestBody RedTemplateSaveReqVO updateReqVO) {
        redTemplateService.updateRedTemplate(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除套红模板")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:red-template:delete')")
    public CommonResult<Boolean> deleteRedTemplate(@RequestParam("id") Long id) {
        redTemplateService.deleteRedTemplate(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除套红模板")
    @PreAuthorize("@ss.hasPermission('oa:red-template:delete')")
    public CommonResult<Boolean> deleteRedTemplateList(@RequestParam("ids") List<Long> ids) {
        redTemplateService.deleteRedTemplateListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得套红模板")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:red-template:query')")
    public CommonResult<RedTemplateRespVO> getRedTemplate(@RequestParam("id") Long id) {
        RedTemplateDO redTemplate = redTemplateService.getRedTemplate(id);
        return success(BeanUtils.toBean(redTemplate, RedTemplateRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得套红模板精简列表（下拉选择用）")
    public CommonResult<List<RedTemplateRespVO>> getRedTemplateSimpleList() {
        List<RedTemplateDO> list = redTemplateService.getRedTemplateList();
        return success(BeanUtils.toBean(list, RedTemplateRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得套红模板分页")
    @PreAuthorize("@ss.hasPermission('oa:red-template:query')")
    public CommonResult<PageResult<RedTemplateRespVO>> getRedTemplatePage(@Valid RedTemplatePageReqVO pageReqVO) {
        PageResult<RedTemplateDO> pageResult = redTemplateService.getRedTemplatePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, RedTemplateRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出套红模板 Excel")
    @PreAuthorize("@ss.hasPermission('oa:red-template:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportRedTemplateExcel(@Valid RedTemplatePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<RedTemplateDO> list = redTemplateService.getRedTemplatePage(pageReqVO).getList();
        ExcelUtils.write(response, "套红模板.xls", "数据", RedTemplateRespVO.class,
                        BeanUtils.toBean(list, RedTemplateRespVO.class));
    }

}
