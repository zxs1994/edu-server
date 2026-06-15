package cn.dh.oa.module.oa.dal.mysql.document;

import java.util.*;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.oa.dal.dataobject.document.DocumentDispatchBillDO;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.oa.module.oa.controller.admin.document.vo.*;

/**
 * 公文发文单 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface DocumentDispatchBillMapper extends BaseMapperX<DocumentDispatchBillDO> {

    default PageResult<DocumentDispatchBillDO> selectPage(DocumentDispatchBillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DocumentDispatchBillDO>()
                .likeIfPresent(DocumentDispatchBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(DocumentDispatchBillDO::getProcessStatus, reqVO.getProcessStatus())
                .likeIfPresent(DocumentDispatchBillDO::getDocTitle, reqVO.getDocTitle())
                .likeIfPresent(DocumentDispatchBillDO::getDocNumber, reqVO.getDocNumber())
                .eqIfPresent(DocumentDispatchBillDO::getSecrecyLevel, reqVO.getSecrecyLevel())
                .eqIfPresent(DocumentDispatchBillDO::getUrgencyLevel, reqVO.getUrgencyLevel())
                .eqIfPresent(DocumentDispatchBillDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(DocumentDispatchBillDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(DocumentDispatchBillDO::getDeptId, reqVO.getDeptId())
                .likeIfPresent(DocumentDispatchBillDO::getDeptName, reqVO.getDeptName())
                .eqIfPresent(DocumentDispatchBillDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(DocumentDispatchBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DocumentDispatchBillDO::getId));
    }

}
