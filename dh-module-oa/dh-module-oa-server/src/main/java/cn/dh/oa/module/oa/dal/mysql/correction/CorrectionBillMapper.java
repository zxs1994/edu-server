package cn.dh.oa.module.oa.dal.mysql.correction;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.oa.dal.dataobject.correction.CorrectionBillDO;
import cn.dh.oa.module.oa.enums.correction.OaBillCorrectionStatusEnum;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.oa.module.oa.controller.admin.correction.vo.CorrectionBillPageReqVO;

import java.util.List;

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

    default CorrectionBillDO selectLatestBySourceProcessInstanceId(String sourceProcessInstanceId) {
        return selectOne(new LambdaQueryWrapperX<CorrectionBillDO>()
                .eq(CorrectionBillDO::getSourceProcessInstanceId, sourceProcessInstanceId)
                .orderByDesc(CorrectionBillDO::getId)
                .last("LIMIT 1"));
    }

    default Long countBySourceBill(String sourceBillType, Long sourceBillId) {
        return selectCount(new LambdaQueryWrapperX<CorrectionBillDO>()
                .eq(CorrectionBillDO::getSourceBillType, sourceBillType)
                .eq(CorrectionBillDO::getSourceBillId, sourceBillId));
    }

    default CorrectionBillDO selectLatestBySourceBill(String sourceBillType, Long sourceBillId) {
        return selectOne(new LambdaQueryWrapperX<CorrectionBillDO>()
                .eq(CorrectionBillDO::getSourceBillType, sourceBillType)
                .eq(CorrectionBillDO::getSourceBillId, sourceBillId)
                .orderByDesc(CorrectionBillDO::getApprovalVersion)
                .orderByDesc(CorrectionBillDO::getId)
                .last("LIMIT 1"));
    }

    default List<CorrectionBillDO> selectListBySourceBill(String sourceBillType, Long sourceBillId) {
        return selectList(new LambdaQueryWrapperX<CorrectionBillDO>()
                .eq(CorrectionBillDO::getSourceBillType, sourceBillType)
                .eq(CorrectionBillDO::getSourceBillId, sourceBillId)
                .orderByAsc(CorrectionBillDO::getApprovalVersion));
    }

    /** 纠错冻结中且尚未重提流程的纠错单（异议纠错 + 理事会决议） */
    default List<CorrectionBillDO> selectListAwaitingObjectionResubmit() {
        return selectList(new LambdaQueryWrapperX<CorrectionBillDO>()
                .eq(CorrectionBillDO::getFreezeStatus, 1)
                .in(CorrectionBillDO::getCorrectionStatus,
                        OaBillCorrectionStatusEnum.IN_PROGRESS.getStatus(),
                        OaBillCorrectionStatusEnum.COUNCIL_OVERRIDE.getStatus())
                .and(w -> w.isNull(CorrectionBillDO::getNewProcessInstanceId)
                        .or()
                        .eq(CorrectionBillDO::getNewProcessInstanceId, ""))
                .orderByDesc(CorrectionBillDO::getRevokeTime)
                .orderByDesc(CorrectionBillDO::getId));
    }

}
