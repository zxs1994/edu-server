package cn.dh.oa.module.oa.dal.mysql.correction;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.oa.dal.dataobject.correction.CorrectionBillDO;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.oa.module.oa.controller.admin.correction.vo.CorrectionBillPageReqVO;

/**
 * 纠错申请单 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface CorrectionBillMapper extends BaseMapperX<CorrectionBillDO> {

    default PageResult<CorrectionBillDO> selectPage(CorrectionBillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CorrectionBillDO>()
                .likeIfPresent(CorrectionBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(CorrectionBillDO::getProcessStatus, reqVO.getProcessStatus())
                .eqIfPresent(CorrectionBillDO::getSourceBillType, reqVO.getSourceBillType())
                .eqIfPresent(CorrectionBillDO::getFreezeStatus, reqVO.getFreezeStatus())
                .eqIfPresent(CorrectionBillDO::getCorrectionStatus, reqVO.getCorrectionStatus())
                .eqIfPresent(CorrectionBillDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(CorrectionBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CorrectionBillDO::getId));
    }

}
