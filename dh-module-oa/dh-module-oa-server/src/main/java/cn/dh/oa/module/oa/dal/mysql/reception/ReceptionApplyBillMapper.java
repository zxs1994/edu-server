package cn.dh.oa.module.oa.dal.mysql.reception;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.module.oa.controller.admin.reception.vo.ReceptionApplyBillPageReqVO;
import cn.dh.oa.module.oa.dal.dataobject.reception.ReceptionApplyBillDO;
import cn.dh.oa.module.oa.service.bill.OaBillApprovalVisibleScope;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReceptionApplyBillMapper extends BaseMapperX<ReceptionApplyBillDO> {

default PageResult<ReceptionApplyBillDO> selectPageByVisibleScope(ReceptionApplyBillPageReqVO reqVO,
                                                        OaBillApprovalVisibleScope visibleScope) {
        LambdaQueryWrapperX<ReceptionApplyBillDO> wrapper = new LambdaQueryWrapperX<ReceptionApplyBillDO>()
                .eqIfPresent(ReceptionApplyBillDO::getId, reqVO.getId())
                .likeIfPresent(ReceptionApplyBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(ReceptionApplyBillDO::getProcessInstanceId, reqVO.getProcessInstanceId())
                .eqIfPresent(ReceptionApplyBillDO::getProcessStatus, reqVO.getProcessStatus())
                .likeIfPresent(ReceptionApplyBillDO::getCause, reqVO.getCause())
                .eqIfPresent(ReceptionApplyBillDO::getDiningStandard, reqVO.getDiningStandard())
                .eqIfPresent(ReceptionApplyBillDO::getCreator, reqVO.getCreator())
                .likeIfPresent(ReceptionApplyBillDO::getCreatorName, reqVO.getCreatorName())
                .likeIfPresent(ReceptionApplyBillDO::getDeptName, reqVO.getDeptName())
                .betweenIfPresent(ReceptionApplyBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ReceptionApplyBillDO::getId);
        if (visibleScope != null) {
            visibleScope.apply(wrapper, ReceptionApplyBillDO::getCreator, ReceptionApplyBillDO::getProcessInstanceId);
        }
        return selectPage(reqVO, wrapper);
    }

}
