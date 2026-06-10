package cn.dh.oa.module.oa.service.travel;

import jakarta.validation.*;
import cn.dh.oa.module.oa.controller.admin.travel.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.travel.TravelApplyBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import java.util.*;

public interface TravelApplyBillService {

    /**
     * 保存差旅申请单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long saveTravelApplyBill(@Valid TravelApplyBillSaveReqVO saveReqVO);

    /**
     * 提交差旅申请单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long submitTravelApplyBill(@Valid TravelApplyBillSaveReqVO saveReqVO);

    /**
     * 创建差旅申请单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTravelApplyBill(@Valid TravelApplyBillSaveReqVO createReqVO);

    /**
     * 更新差旅申请单
     *
     * @param updateReqVO 更新信息
     */
    void updateTravelApplyBill(@Valid TravelApplyBillSaveReqVO updateReqVO);

    /**
     * 删除差旅申请单
     *
     * @param id 编号
     */
    void deleteTravelApplyBill(Long id);

    /**
     * 批量删除差旅申请单
     *
     * @param ids 编号列表
     */
    void deleteTravelApplyBillListByIds(List<Long> ids);

    /**
     * 获得差旅申请单
     *
     * @param id 编号
     * @return 差旅申请单
     */
    TravelApplyBillDO getTravelApplyBill(Long id);

    /**
     * 获得差旅申请单详情
     *
     * @param id 编号
     * @return 差旅申请单详情
     */
    TravelApplyBillRespVO getTravelApplyBillInfo(Long id);

    /**
     * 获得差旅申请单分页
     *
     * @param pageReqVO 分页查询
     * @return 差旅申请单分页
     */
    PageResult<TravelApplyBillDO> getTravelApplyBillPage(TravelApplyBillPageReqVO pageReqVO);

}
