package cn.dh.edu.module.edu.dal.mysql.student;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.module.edu.controller.admin.student.vo.StudentPageReqVO;
import cn.dh.edu.module.edu.dal.dataobject.student.StudentDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 学生档案 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface StudentMapper extends BaseMapperX<StudentDO> {

    default PageResult<StudentDO> selectPage(StudentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StudentDO>()
                .likeIfPresent(StudentDO::getStudentNo, reqVO.getStudentNo())
                .likeIfPresent(StudentDO::getUsername, reqVO.getUsername())
                .likeIfPresent(StudentDO::getName, reqVO.getName())
                .likeIfPresent(StudentDO::getCollege, reqVO.getCollege())
                .likeIfPresent(StudentDO::getMajor, reqVO.getMajor())
                .likeIfPresent(StudentDO::getClassName, reqVO.getClassName())
                .eqIfPresent(StudentDO::getSchoolStatus, reqVO.getSchoolStatus())
                .eqIfPresent(StudentDO::getEnrollYear, reqVO.getEnrollYear())
                .betweenIfPresent(StudentDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(StudentDO::getId));
    }

    default StudentDO selectByStudentNo(String studentNo) {
        return selectOne(StudentDO::getStudentNo, studentNo);
    }

    default StudentDO selectByUsername(String username) {
        return selectOne(StudentDO::getUsername, username);
    }

    default StudentDO selectByUserId(Long userId) {
        return selectOne(StudentDO::getUserId, userId);
    }

    /**
     * 查询学生账号最大流水号（S0001 中的数字部分；含软删）
     */
    @Select("SELECT MAX(CAST(SUBSTRING(username, 2) AS UNSIGNED)) "
            + "FROM edu_student WHERE username REGEXP '^S[0-9]+$'")
    Long selectMaxAccountSequence();

}
