package cn.dh.edu.module.edu.dal.mysql.teacher;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.module.edu.controller.admin.teacher.vo.TeacherPageReqVO;
import cn.dh.edu.module.edu.dal.dataobject.teacher.TeacherDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 教培档案 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface TeacherMapper extends BaseMapperX<TeacherDO> {

    default PageResult<TeacherDO> selectPage(TeacherPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TeacherDO>()
                .likeIfPresent(TeacherDO::getUsername, reqVO.getUsername())
                .likeIfPresent(TeacherDO::getName, reqVO.getName())
                .eqIfPresent(TeacherDO::getTitle, reqVO.getTitle())
                .eqIfPresent(TeacherDO::getRewardStandard, reqVO.getRewardStandard())
                .likeIfPresent(TeacherDO::getMobile, reqVO.getMobile())
                .betweenIfPresent(TeacherDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TeacherDO::getId));
    }

    default TeacherDO selectByUsername(String username) {
        return selectOne(TeacherDO::getUsername, username);
    }

    default TeacherDO selectByUserId(Long userId) {
        return selectOne(TeacherDO::getUserId, userId);
    }

    /**
     * 查询教培账号最大流水号（T0001 中的数字部分；含软删）
     */
    @Select("SELECT MAX(CAST(SUBSTRING(username, 2) AS UNSIGNED)) "
            + "FROM edu_teacher WHERE username REGEXP '^T[0-9]+$'")
    Long selectMaxAccountSequence();

}
