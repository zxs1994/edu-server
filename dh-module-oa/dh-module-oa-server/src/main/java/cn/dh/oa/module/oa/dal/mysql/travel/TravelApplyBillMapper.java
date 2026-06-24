package cn.dh.oa.module.oa.dal.mysql.travel;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.oa.dal.dataobject.travel.TravelApplyBillDO;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.oa.module.oa.controller.admin.travel.vo.TravelApplyBillPageReqVO;

@Mapper
public interface TravelApplyBillMapper extends BaseMapperX<TravelApplyBillDO> {

    default PageResult<TravelApplyBillDO> selectPage(TravelApplyBillPageReqVO reqVO) {
        LambdaQueryWrapperX<TravelApplyBillDO> wrapper = new LambdaQueryWrapperX<TravelApplyBillDO>()
                .likeIfPresent(TravelApplyBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(TravelApplyBillDO::getProcessStatus, reqVO.getProcessStatus())
                .likeIfPresent(TravelApplyBillDO::getDeptName, reqVO.getDeptName())
                .eqIfPresent(TravelApplyBillDO::getCreator, reqVO.getCreator())
                .eqIfPresent(TravelApplyBillDO::getReimbursementStatus, reqVO.getReimbursementStatus())
                .betweenIfPresent(TravelApplyBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TravelApplyBillDO::getId);
        if (Boolean.TRUE.equals(reqVO.getExcludeLinkedToExpense())) {
            if (reqVO.getExcludeExpenseBillId() != null) {
                wrapper.and(w -> w.isNull(TravelApplyBillDO::getLinkedExpenseBillId)
                        .or()
                        .eq(TravelApplyBillDO::getLinkedExpenseBillId, reqVO.getExcludeExpenseBillId()));
            } else {
                wrapper.isNull(TravelApplyBillDO::getLinkedExpenseBillId);
            }
        }
        return selectPage(reqVO, wrapper);
    }

}
