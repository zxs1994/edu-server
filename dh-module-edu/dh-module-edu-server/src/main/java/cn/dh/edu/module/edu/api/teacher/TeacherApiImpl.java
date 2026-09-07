package cn.dh.edu.module.edu.api.teacher;

import cn.hutool.core.util.StrUtil;
import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.module.edu.api.teacher.dto.TeacherUpdateReqDTO;
import cn.dh.edu.module.edu.dal.dataobject.teacher.TeacherDO;
import cn.dh.edu.module.edu.dal.mysql.teacher.TeacherMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.framework.common.pojo.CommonResult.success;
import static cn.dh.edu.module.edu.enums.ErrorCodeConstants.TEACHER_MOBILE_REQUIRED;

@RestController
@Validated
public class TeacherApiImpl implements TeacherApi {

    @Resource
    private TeacherMapper teacherMapper;

    @Override
    public CommonResult<Boolean> updateTeacherByUserId(Long userId, TeacherUpdateReqDTO updateReqDTO) {
        TeacherDO teacher = teacherMapper.selectByUserId(userId);
        if (teacher == null) {
            return success(false);
        }
        if (!Boolean.TRUE.equals(teacher.getUserGenerated())) {
            return success(false);
        }
        if (StrUtil.isBlank(updateReqDTO.getMobile())) {
            throw exception(TEACHER_MOBILE_REQUIRED);
        }

        // 仅更新用户侧可同步字段，避免 updateById 因 rewardStandard 的 ALWAYS 策略把报酬标准清空
        teacherMapper.update(null, new LambdaUpdateWrapper<TeacherDO>()
                .eq(TeacherDO::getId, teacher.getId())
                .set(TeacherDO::getName, updateReqDTO.getName())
                .set(TeacherDO::getMobile, updateReqDTO.getMobile())
                .set(TeacherDO::getSex, updateReqDTO.getSex())
                .set(TeacherDO::getRemark, updateReqDTO.getRemark()));
        return success(true);
    }

    @Override
    public CommonResult<Boolean> deleteTeacherByUserId(Long userId) {
        TeacherDO teacher = teacherMapper.selectByUserId(userId);
        if (teacher == null) {
            return success(false);
        }
        teacherMapper.deleteById(teacher.getId());
        return success(true);
    }

}
