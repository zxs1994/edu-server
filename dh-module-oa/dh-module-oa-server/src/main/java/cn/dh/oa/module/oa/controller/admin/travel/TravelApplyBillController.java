package cn.dh.oa.module.oa.controller.admin.travel;

import cn.dh.oa.framework.apilog.core.annotation.ApiAccessLog;
import cn.dh.oa.framework.common.pojo.CommonResult;
import cn.dh.oa.framework.common.pojo.PageParam;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import cn.dh.oa.framework.excel.core.util.ExcelUtils;
import cn.dh.oa.module.oa.controller.admin.travel.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.travel.TravelApplyBillDO;
import cn.dh.oa.module.oa.service.travel.TravelApplyBillService;
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

@Tag(name = "管理后台 - 差旅申请单")
@RestController
@RequestMapping("/oa/travel-apply-bill")
@Validated
public class TravelApplyBillController {

    @Resource
    private TravelApplyBillService travelApplyBillService;

    @PostMapping("/create")
    @Operation(summary = "创建差旅申请单")
    @PreAuthorize("@ss.hasPermission('oa:travel-apply-bill:create')")
    public CommonResult<Long> createTravelApplyBill(@Valid @RequestBody TravelApplyBillSaveReqVO createReqVO) {
        return success(travelApplyBillService.createTravelApplyBill(createReqVO));
    }

    @PostMapping("/save")
    @Operation(summary = "保存差旅申请单")
    @PreAuthorize("@ss.hasPermission('oa:travel-apply-bill:create')")
    public CommonResult<Long> saveTravelApplyBill(@Valid @RequestBody TravelApplyBillSaveReqVO saveReqVO) {
        return success(travelApplyBillService.saveTravelApplyBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交差旅申请单")
    @PreAuthorize("@ss.hasPermission('oa:travel-apply-bill:create')")
    public CommonResult<Long> submitTravelApplyBill(@Valid @RequestBody TravelApplyBillSaveReqVO submitReqVO) {
        return success(travelApplyBillService.submitTravelApplyBill(submitReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新差旅申请单")
    @PreAuthorize("@ss.hasPermission('oa:travel-apply-bill:update')")
    public CommonResult<Boolean> updateTravelApplyBill(@Valid @RequestBody TravelApplyBillSaveReqVO updateReqVO) {
        travelApplyBillService.updateTravelApplyBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除差旅申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:travel-apply-bill:delete')")
    public CommonResult<Boolean> deleteTravelApplyBill(@RequestParam("id") Long id) {
        travelApplyBillService.deleteTravelApplyBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除差旅申请单")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('oa:travel-apply-bill:delete')")
    public CommonResult<Boolean> deleteTravelApplyBillList(@RequestParam("ids") List<Long> ids) {
        travelApplyBillService.deleteTravelApplyBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得差旅申请单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:travel-apply-bill:query')")
    public CommonResult<TravelApplyBillRespVO> getTravelApplyBill(@RequestParam("id") Long id) {
        TravelApplyBillRespVO respVO = travelApplyBillService.getTravelApplyBillInfo(id);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得差旅申请单分页")
    @PreAuthorize("@ss.hasPermission('oa:travel-apply-bill:query')")
    public CommonResult<PageResult<TravelApplyBillRespVO>> getTravelApplyBillPage(@Valid TravelApplyBillPageReqVO pageReqVO) {
        PageResult<TravelApplyBillDO> pageResult = travelApplyBillService.getTravelApplyBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, TravelApplyBillRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出差旅申请单 Excel")
    @PreAuthorize("@ss.hasPermission('oa:travel-apply-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTravelApplyBillExcel(@Valid TravelApplyBillPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TravelApplyBillDO> list = travelApplyBillService.getTravelApplyBillPage(pageReqVO).getList();
        ExcelUtils.write(response, "差旅申请单.xls", "数据", TravelApplyBillRespVO.class,
                        BeanUtils.toBean(list, TravelApplyBillRespVO.class));
    }

}
