package cn.dh.oa.module.oa.dal.mysql.template;

import java.util.*;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.oa.dal.dataobject.template.RedTemplateDO;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.oa.module.oa.controller.admin.template.vo.*;

/**
 * 套红模板 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface RedTemplateMapper extends BaseMapperX<RedTemplateDO> {

    default PageResult<RedTemplateDO> selectPage(RedTemplatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RedTemplateDO>()
                .likeIfPresent(RedTemplateDO::getTemplateName, reqVO.getTemplateName())
                .likeIfPresent(RedTemplateDO::getOrgName, reqVO.getOrgName())
                .eqIfPresent(RedTemplateDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(RedTemplateDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(RedTemplateDO::getSort)
                .orderByDesc(RedTemplateDO::getId));
    }

    default List<RedTemplateDO> selectListByStatus(Integer status) {
        return selectList(RedTemplateDO::getStatus, status);
    }

}
