package cn.dh.oa.module.oa.dal.mysql.seal;

import java.util.*;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.oa.dal.dataobject.seal.SealDO;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.oa.module.oa.controller.admin.seal.vo.*;

/**
 * 印章信息 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface SealMapper extends BaseMapperX<SealDO> {

    default PageResult<SealDO> selectPage(SealPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SealDO>()
                .likeIfPresent(SealDO::getSealNo, reqVO.getSealNo())
                .likeIfPresent(SealDO::getSealName, reqVO.getSealName())
                .eqIfPresent(SealDO::getSealType, reqVO.getSealType())
                .eqIfPresent(SealDO::getSealCls, reqVO.getSealCls())
                .eqIfPresent(SealDO::getKeeperId, reqVO.getKeeperId())
                .likeIfPresent(SealDO::getKeeperName, reqVO.getKeeperName())
                .eqIfPresent(SealDO::getKeeperDeptId, reqVO.getKeeperDeptId())
                .likeIfPresent(SealDO::getKeeperDeptName, reqVO.getKeeperDeptName())
                .eqIfPresent(SealDO::getStatus, reqVO.getStatus())
                .neIfPresent(SealDO::getStatus, reqVO.getStatusNe())
                .betweenIfPresent(SealDO::getPurchaseDate, reqVO.getPurchaseDate())
                .betweenIfPresent(SealDO::getEnableDate, reqVO.getEnableDate())
                .betweenIfPresent(SealDO::getDisableDate, reqVO.getDisableDate())
                .eqIfPresent(SealDO::getSort, reqVO.getSort())
                .eqIfPresent(SealDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(SealDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(SealDO::getSort)
                .orderByAsc(SealDO::getCreateTime));
    }

    default SealDO selectBySealNo(String sealNo) {
        return selectOne(SealDO::getSealNo, sealNo);
    }

}

