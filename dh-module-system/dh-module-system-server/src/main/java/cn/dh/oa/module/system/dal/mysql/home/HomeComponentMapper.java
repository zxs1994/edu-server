package cn.dh.oa.module.system.dal.mysql.home;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.module.system.controller.admin.home.vo.component.HomeComponentPageReqVO;
import cn.dh.oa.module.system.dal.dataobject.home.HomeComponentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 首页组件 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface HomeComponentMapper extends BaseMapperX<HomeComponentDO> {

    default PageResult<HomeComponentDO> selectPage(HomeComponentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HomeComponentDO>()
                .likeIfPresent(HomeComponentDO::getName, reqVO.getName())
                .eqIfPresent(HomeComponentDO::getCode, reqVO.getCode())
                .eqIfPresent(HomeComponentDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(HomeComponentDO::getStatus, reqVO.getStatus())
                .orderByAsc(HomeComponentDO::getSort)
                .orderByDesc(HomeComponentDO::getId));
    }

    default HomeComponentDO selectByCode(String code) {
        return selectOne(HomeComponentDO::getCode, code);
    }

    default List<HomeComponentDO> selectByCategoryId(Long categoryId) {
        return selectList(HomeComponentDO::getCategoryId, categoryId);
    }

}
