package cn.dh.oa.module.system.dal.mysql.home;

import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.module.system.dal.dataobject.home.HomeComponentCategoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 首页组件分类 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface HomeComponentCategoryMapper extends BaseMapperX<HomeComponentCategoryDO> {

    default HomeComponentCategoryDO selectByCode(String code) {
        return selectOne(HomeComponentCategoryDO::getCode, code);
    }

    default List<HomeComponentCategoryDO> selectAllOrdered() {
        return selectList(new LambdaQueryWrapperX<HomeComponentCategoryDO>()
                .orderByAsc(HomeComponentCategoryDO::getSort));
    }

}
