package cn.dh.edu.module.edu.service.teacher;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.module.edu.controller.admin.teacher.vo.TeacherCreateRespVO;
import cn.dh.edu.module.edu.controller.admin.teacher.vo.TeacherPageReqVO;
import cn.dh.edu.module.edu.controller.admin.teacher.vo.TeacherRespVO;
import cn.dh.edu.module.edu.controller.admin.teacher.vo.TeacherSaveReqVO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 教培档案 Service 接口
 *
 * @author 鼎衡
 */
public interface TeacherService {

    TeacherCreateRespVO createTeacher(@Valid TeacherSaveReqVO createReqVO);

    void updateTeacher(@Valid TeacherSaveReqVO updateReqVO);

    void deleteTeacher(Long id);

    void deleteTeacherList(List<Long> ids);

    TeacherRespVO getTeacher(Long id);

    /** 按关联用户ID获得教培档案 */
    TeacherRespVO getTeacherByUserId(Long userId);

    PageResult<TeacherRespVO> getTeacherPage(TeacherPageReqVO pageReqVO);

}
