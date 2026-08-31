package cn.dh.edu.module.edu.api.student;

import cn.hutool.core.util.StrUtil;
import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.module.edu.api.student.dto.StudentUpdateReqDTO;
import cn.dh.edu.module.edu.dal.dataobject.student.StudentDO;
import cn.dh.edu.module.edu.dal.mysql.student.StudentMapper;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.framework.common.pojo.CommonResult.success;
import static cn.dh.edu.module.edu.enums.ErrorCodeConstants.STUDENT_MOBILE_REQUIRED;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class StudentApiImpl implements StudentApi {

    @Resource
    private StudentMapper studentMapper;

    @Override
    public CommonResult<Boolean> updateStudentByUserId(Long userId, StudentUpdateReqDTO updateReqDTO) {
        StudentDO student = studentMapper.selectByUserId(userId);
        if (student == null) {
            return success(false);
        }
        if (!Boolean.TRUE.equals(student.getUserGenerated())) {
            return success(false);
        }
        if (StrUtil.isBlank(updateReqDTO.getMobile())) {
            throw exception(STUDENT_MOBILE_REQUIRED);
        }

        StudentDO updateObj = new StudentDO();
        updateObj.setId(student.getId());
        updateObj.setName(updateReqDTO.getName());
        updateObj.setMobile(updateReqDTO.getMobile());
        updateObj.setSex(updateReqDTO.getSex());
        updateObj.setRemark(updateReqDTO.getRemark());
        studentMapper.updateById(updateObj);
        return success(true);
    }

    @Override
    public CommonResult<Boolean> deleteStudentByUserId(Long userId) {
        StudentDO student = studentMapper.selectByUserId(userId);
        if (student == null) {
            return success(false);
        }
        studentMapper.deleteById(student.getId());
        return success(true);
    }

}
