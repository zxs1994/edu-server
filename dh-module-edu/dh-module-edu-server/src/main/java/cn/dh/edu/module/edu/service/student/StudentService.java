package cn.dh.edu.module.edu.service.student;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.module.edu.controller.admin.student.vo.StudentCreateRespVO;
import cn.dh.edu.module.edu.controller.admin.student.vo.StudentPageReqVO;
import cn.dh.edu.module.edu.controller.admin.student.vo.StudentRespVO;
import cn.dh.edu.module.edu.controller.admin.student.vo.StudentSaveReqVO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 学生档案 Service 接口
 *
 * @author 鼎衡
 */
public interface StudentService {

    /**
     * 创建学生档案
     *
     * @param createReqVO 创建信息
     * @return 创建结果（含登录账号与初始密码）
     */
    StudentCreateRespVO createStudent(@Valid StudentSaveReqVO createReqVO);

    /**
     * 更新学生档案
     *
     * @param updateReqVO 更新信息
     */
    void updateStudent(@Valid StudentSaveReqVO updateReqVO);

    /**
     * 删除学生档案
     *
     * @param id 编号
     */
    void deleteStudent(Long id);

    /**
     * 批量删除学生档案
     *
     * @param ids 编号列表
     */
    void deleteStudentList(List<Long> ids);

    /**
     * 获得学生档案
     *
     * @param id 编号
     * @return 学生档案
     */
    StudentRespVO getStudent(Long id);

    /**
     * 获得学生档案分页
     *
     * @param pageReqVO 分页查询
     * @return 学生档案分页
     */
    PageResult<StudentRespVO> getStudentPage(StudentPageReqVO pageReqVO);

}
