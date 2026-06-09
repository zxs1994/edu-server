package cn.dh.oa.module.system.dal.mysql.notice;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.module.system.controller.admin.notice.vo.NoticePageReqVO;
import cn.dh.oa.module.system.dal.dataobject.notice.NoticeDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapperX<NoticeDO> {

    default PageResult<NoticeDO> selectPage(NoticePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<NoticeDO>()
                .likeIfPresent(NoticeDO::getTitle, reqVO.getTitle())
                .eqIfPresent(NoticeDO::getType, reqVO.getType())
                .eqIfPresent(NoticeDO::getStatus, reqVO.getStatus())
                .eqIfPresent(NoticeDO::getIsImportant, reqVO.getIsImportant())
                .orderByDesc(NoticeDO::getId));
    }

}
