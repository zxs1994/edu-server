package cn.dh.oa.module.oa.service.car;

import java.util.*;
import jakarta.validation.*;
import cn.dh.oa.module.oa.controller.admin.car.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.car.CarReturnBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.pojo.PageParam;

/**
 * 还车申请单 Service 接口
 *
 * @author 鼎衡
 */
public interface CarReturnBillService {

    /**
     * 保存还车申请单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long saveCarReturnBill(@Valid CarReturnBillSaveReqVO saveReqVO);

    /**
     * 创建还车申请单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCarReturnBill(@Valid CarReturnBillSaveReqVO createReqVO);

    /**
     * 提交还车申请单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long submitCarReturnBill(CarReturnBillSaveReqVO createReqVO);

    /**
     * 更新还车申请单
     *
     * @param updateReqVO 更新信息
     */
    void updateCarReturnBill(@Valid CarReturnBillSaveReqVO updateReqVO);

    /**
     * 删除还车申请单
     *
     * @param id 编号
     */
    void deleteCarReturnBill(Long id);

    /**
    * 批量删除还车申请单
    *
    * @param ids 编号
    */
    void deleteCarReturnBillListByIds(List<Long> ids);

    /**
     * 获得还车申请单
     *
     * @param id 编号
     * @return 还车申请单
     */
    CarReturnBillDO getCarReturnBill(Long id);

    /**
     * 获得还车申请单（包含附件信息）
     *
     * @param id 编号
     * @return 还车申请单响应VO
     */
    CarReturnBillRespVO getCarReturnBillInfo(Long id);

    /**
     * 获得还车申请单分页
     *
     * @param pageReqVO 分页查询
     * @return 还车申请单分页
     */
    PageResult<CarReturnBillDO> getCarReturnBillPage(CarReturnBillPageReqVO pageReqVO);


}