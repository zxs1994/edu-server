package cn.dh.oa.module.oa.dal.mysql.seal;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.oa.dal.dataobject.seal.SealApplyBillDO;
import cn.dh.oa.module.oa.service.bill.OaBillApprovalVisibleScope;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.oa.module.oa.controller.admin.seal.vo.*;

/**
 * 用印申请单 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface SealApplyBillMapper extends BaseMapperX<SealApplyBillDO> {

default PageResult<SealApplyBillDO> selectPageByVisibleScope(SealApplyBillPageReqVO reqVO,
                                                        OaBillApprovalVisibleScope visibleScope) {
        LambdaQueryWrapperX<SealApplyBillDO> wrapper = new LambdaQueryWrapperX<SealApplyBillDO>()
                .likeIfPresent(SealApplyBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(SealApplyBillDO::getProcessStatus, reqVO.getProcessStatus())
                .eqIfPresent(SealApplyBillDO::getSealId, reqVO.getSealId())
                .likeIfPresent(SealApplyBillDO::getSealNo, reqVO.getSealNo())
                .likeIfPresent(SealApplyBillDO::getSealName, reqVO.getSealName())
                .eqIfPresent(SealApplyBillDO::getUseType, reqVO.getUseType())
                .eqIfPresent(SealApplyBillDO::getUseMode, reqVO.getUseMode())
                .eqIfPresent(SealApplyBillDO::getUseStatus, reqVO.getUseStatus())
                .eqIfPresent(SealApplyBillDO::getIsUrgent, reqVO.getIsUrgent())
                .eqIfPresent(SealApplyBillDO::getDeptId, reqVO.getDeptId())
                .likeIfPresent(SealApplyBillDO::getDeptName, reqVO.getDeptName())
                .betweenIfPresent(SealApplyBillDO::getExpectedUseTime, reqVO.getExpectedUseTime())
                .eqIfPresent(SealApplyBillDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(SealApplyBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SealApplyBillDO::getId);
        if (visibleScope != null) {
            visibleScope.apply(wrapper, SealApplyBillDO::getCreator, SealApplyBillDO::getProcessInstanceId);
        }
        return selectPage(reqVO, wrapper);
    }

}
