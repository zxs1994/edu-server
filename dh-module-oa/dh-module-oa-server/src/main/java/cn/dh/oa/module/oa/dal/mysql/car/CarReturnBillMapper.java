package cn.dh.oa.module.oa.dal.mysql.car;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.oa.dal.dataobject.car.CarReturnBillDO;
import cn.dh.oa.module.oa.service.bill.OaBillApprovalVisibleScope;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.oa.module.oa.controller.admin.car.vo.*;

/**
 * 还车申请单 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface CarReturnBillMapper extends BaseMapperX<CarReturnBillDO> {

default PageResult<CarReturnBillDO> selectPageByVisibleScope(CarReturnBillPageReqVO reqVO,
                                                        OaBillApprovalVisibleScope visibleScope) {
        LambdaQueryWrapperX<CarReturnBillDO> wrapper = new LambdaQueryWrapperX<CarReturnBillDO>()
                .eqIfPresent(CarReturnBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(CarReturnBillDO::getProcessInstanceId, reqVO.getProcessInstanceId())
                .eqIfPresent(CarReturnBillDO::getProcessStatus, reqVO.getProcessStatus())
                .eqIfPresent(CarReturnBillDO::getApplyBill, reqVO.getApplyBill())
                .eqIfPresent(CarReturnBillDO::getCarId, reqVO.getCarId())
                .eqIfPresent(CarReturnBillDO::getCarNo, reqVO.getCarNo())
                .betweenIfPresent(CarReturnBillDO::getGoTime, reqVO.getGoTime())
                .betweenIfPresent(CarReturnBillDO::getReturnTime, reqVO.getReturnTime())
                .eqIfPresent(CarReturnBillDO::getGoArea, reqVO.getGoArea())
                .eqIfPresent(CarReturnBillDO::getReturnArea, reqVO.getReturnArea())
                .eqIfPresent(CarReturnBillDO::getCause, reqVO.getCause())
                .eqIfPresent(CarReturnBillDO::getApplyer, reqVO.getApplyer())
                .eqIfPresent(CarReturnBillDO::getPassenger, reqVO.getPassenger())
                .eqIfPresent(CarReturnBillDO::getRemark, reqVO.getRemark())
                .eqIfPresent(CarReturnBillDO::getCreator, reqVO.getCreator())
                .likeIfPresent(CarReturnBillDO::getCreatorName, reqVO.getCreatorName())
                .betweenIfPresent(CarReturnBillDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(CarReturnBillDO::getParentId, reqVO.getParentId())
                .eqIfPresent(CarReturnBillDO::getDeptId, reqVO.getDeptId())
                .likeIfPresent(CarReturnBillDO::getDeptName, reqVO.getDeptName())
                .orderByDesc(CarReturnBillDO::getId);
        if (visibleScope != null) {
            visibleScope.apply(wrapper, CarReturnBillDO::getCreator, CarReturnBillDO::getProcessInstanceId);
        }
        return selectPage(reqVO, wrapper);
    }

}
