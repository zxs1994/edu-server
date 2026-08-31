package cn.dh.edu.module.edu.controller.admin.dashboard;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.module.edu.controller.admin.dashboard.vo.EduOverviewRespVO;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityMapper;
import cn.dh.edu.module.edu.dal.mysql.student.StudentMapper;
import cn.dh.edu.module.edu.dal.mysql.teacher.TeacherMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.dh.edu.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 教培信息总览")
@RestController
@RequestMapping("/edu/dashboard")
@Validated
public class EduDashboardController {

    @Resource
    private StudentMapper studentMapper;
    @Resource
    private TeacherMapper teacherMapper;
    @Resource
    private ActivityMapper activityMapper;

    @GetMapping("/overview")
    @Operation(summary = "获得信息总览")
    @PreAuthorize("@ss.hasPermission('edu:dashboard:overview')")
    public CommonResult<EduOverviewRespVO> getOverview() {
        EduOverviewRespVO respVO = new EduOverviewRespVO();
        respVO.setStudentCount(studentMapper.selectCount());
        respVO.setTeacherCount(teacherMapper.selectCount());
        respVO.setActivityCount(activityMapper.selectCount());
        // 预算执行率后续接入
        respVO.setBudgetExecRate(null);
        return success(respVO);
    }

}
