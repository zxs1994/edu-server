package cn.dh.oa.module.oa.dal.mysql.incoming;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.oa.dal.dataobject.incoming.IncomingDocumentBillDO;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.oa.module.oa.controller.admin.incoming.vo.IncomingDocumentBillPageReqVO;

@Mapper
public interface IncomingDocumentBillMapper extends BaseMapperX<IncomingDocumentBillDO> {

    default PageResult<IncomingDocumentBillDO> selectPage(IncomingDocumentBillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<IncomingDocumentBillDO>()
                .likeIfPresent(IncomingDocumentBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(IncomingDocumentBillDO::getProcessStatus, reqVO.getProcessStatus())
                .likeIfPresent(IncomingDocumentBillDO::getDocTitle, reqVO.getDocTitle())
                .likeIfPresent(IncomingDocumentBillDO::getDocNumber, reqVO.getDocNumber())
                .likeIfPresent(IncomingDocumentBillDO::getSender, reqVO.getSender())
                .eqIfPresent(IncomingDocumentBillDO::getDocType, reqVO.getDocType())
                .eqIfPresent(IncomingDocumentBillDO::getUrgencyLevel, reqVO.getUrgencyLevel())
                .eqIfPresent(IncomingDocumentBillDO::getHandlingStatus, reqVO.getHandlingStatus())
                .eqIfPresent(IncomingDocumentBillDO::getIsImportant, reqVO.getIsImportant())
                .eqIfPresent(IncomingDocumentBillDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(IncomingDocumentBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(IncomingDocumentBillDO::getId));
    }

}
