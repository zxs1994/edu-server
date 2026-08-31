package cn.dh.edu.module.system.dal.mysql.dept;

import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import cn.dh.edu.module.system.dal.dataobject.dept.DeptDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface DeptMapper extends BaseMapperX<DeptDO> {

    default List<DeptDO> selectList(DeptListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<DeptDO>()
                .eqIfPresent(DeptDO::getOrgType, reqVO.getOrgType())
                .likeIfPresent(DeptDO::getName, reqVO.getName())
                .eqIfPresent(DeptDO::getStatus, reqVO.getStatus()));
    }

    default DeptDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(DeptDO::getParentId, parentId, DeptDO::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(DeptDO::getParentId, parentId);
    }

    default List<DeptDO> selectListByParentId(Collection<Long> parentIds) {
        return selectList(DeptDO::getParentId, parentIds);
    }

    default List<DeptDO> selectListByLeaderUserId(Long id) {
        return selectList(DeptDO::getLeaderUserId, id);
    }

    default DeptDO selectRootCompany() {
        return selectOne(new LambdaQueryWrapperX<DeptDO>()
                .eq(DeptDO::getParentId, DeptDO.PARENT_ID_ROOT)
                .eq(DeptDO::getOrgType, cn.dh.edu.module.system.enums.OrgTypeEnum.COMPANY.getValue())
                .orderByDesc(DeptDO::getId)
                .last("LIMIT 1"));
    }

}
