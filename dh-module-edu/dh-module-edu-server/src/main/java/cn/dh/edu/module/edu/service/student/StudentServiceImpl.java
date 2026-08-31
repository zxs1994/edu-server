package cn.dh.edu.module.edu.service.student;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.module.edu.controller.admin.student.vo.StudentCreateRespVO;
import cn.dh.edu.module.edu.controller.admin.student.vo.StudentPageReqVO;
import cn.dh.edu.module.edu.controller.admin.student.vo.StudentRespVO;
import cn.dh.edu.module.edu.controller.admin.student.vo.StudentSaveReqVO;
import cn.dh.edu.module.edu.dal.dataobject.student.StudentDO;
import cn.dh.edu.module.edu.dal.mysql.student.StudentMapper;
import cn.dh.edu.module.infra.api.config.ConfigApi;
import cn.dh.edu.module.system.api.permission.PermissionApi;
import cn.dh.edu.module.system.api.permission.RoleApi;
import cn.dh.edu.module.system.api.user.AdminUserApi;
import cn.dh.edu.module.system.api.user.dto.AdminUserCreateReqDTO;
import cn.dh.edu.module.system.api.user.dto.AdminUserUpdateReqDTO;
import cn.dh.edu.module.system.enums.permission.RoleCodeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.module.edu.enums.ErrorCodeConstants.*;

/**
 * 学生档案 Service 实现类
 *
 * @author 鼎衡
 */
@Service
@Validated
public class StudentServiceImpl implements StudentService {

    private static final String INIT_PASSWORD_KEY = "system.user.init-password";
    private static final String DEFAULT_INIT_PASSWORD = "123456";
    /** 学生账号：S0001 起，至少 4 位数字补零 */
    private static final String STUDENT_ACCOUNT_PREFIX = "S";

    @Resource
    private StudentMapper studentMapper;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private RoleApi roleApi;
    @Resource
    private PermissionApi permissionApi;
    @Resource
    private ConfigApi configApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentCreateRespVO createStudent(StudentSaveReqVO createReqVO) {
        createReqVO.setUsername(generateStudentAccount());
        validateStudentNoUnique(null, createReqVO.getStudentNo());
        validateUsernameFormat(createReqVO.getUsername());
        validateUsernameUnique(null, createReqVO.getUsername());

        // 1. 插入学生档案
        StudentDO student = BeanUtils.toBean(createReqVO, StudentDO.class);
        studentMapper.insert(student);

        // 2. 自动创建「学生」角色账号，并回写关联
        String initPassword = getInitPassword();
        Long userId = createStudentUser(createReqVO, initPassword);
        StudentDO updateObj = new StudentDO();
        updateObj.setId(student.getId());
        updateObj.setUserId(userId);
        updateObj.setUserGenerated(true);
        studentMapper.updateById(updateObj);

        return new StudentCreateRespVO(student.getId(), createReqVO.getUsername(), initPassword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStudent(StudentSaveReqVO updateReqVO) {
        StudentDO oldStudent = studentMapper.selectById(updateReqVO.getId());
        if (oldStudent == null) {
            throw exception(STUDENT_NOT_EXISTS);
        }
        validateStudentNoUnique(updateReqVO.getId(), updateReqVO.getStudentNo());
        // 登录账号创建后不可修改
        updateReqVO.setUsername(oldStudent.getUsername());

        StudentDO updateObj = BeanUtils.toBean(updateReqVO, StudentDO.class);
        studentMapper.updateById(updateObj);

        // 已生成账号时，档案为主同步到用户（含登录名）
        if (Boolean.TRUE.equals(oldStudent.getUserGenerated()) && oldStudent.getUserId() != null) {
            syncStudentToUser(updateReqVO, oldStudent.getUserId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudent(Long id) {
        StudentDO student = studentMapper.selectById(id);
        if (student == null) {
            throw exception(STUDENT_NOT_EXISTS);
        }
        // 已关联账号：删用户会级联删学生档案；未关联则直接删档案
        if (Boolean.TRUE.equals(student.getUserGenerated()) && student.getUserId() != null) {
            adminUserApi.deleteUser(student.getUserId());
            return;
        }
        studentMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudentList(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<StudentDO> students = studentMapper.selectBatchIds(ids);
        List<Long> userIds = students.stream()
                .filter(s -> Boolean.TRUE.equals(s.getUserGenerated()) && s.getUserId() != null)
                .map(StudentDO::getUserId)
                .toList();
        if (CollUtil.isNotEmpty(userIds)) {
            // 删用户会级联删对应学生档案
            adminUserApi.deleteUserList(userIds);
        }
        List<Long> archiveOnlyIds = students.stream()
                .filter(s -> !(Boolean.TRUE.equals(s.getUserGenerated()) && s.getUserId() != null))
                .map(StudentDO::getId)
                .toList();
        if (CollUtil.isNotEmpty(archiveOnlyIds)) {
            studentMapper.deleteByIds(archiveOnlyIds);
        }
    }

    @Override
    public StudentRespVO getStudent(Long id) {
        StudentDO student = studentMapper.selectById(id);
        return BeanUtils.toBean(student, StudentRespVO.class);
    }

    @Override
    public PageResult<StudentRespVO> getStudentPage(StudentPageReqVO pageReqVO) {
        PageResult<StudentDO> pageResult = studentMapper.selectPage(pageReqVO);
        return BeanUtils.toBean(pageResult, StudentRespVO.class);
    }

    private Long createStudentUser(StudentSaveReqVO student, String initPassword) {
        Long roleId = roleApi.getRoleIdByCode(RoleCodeEnum.STUDENT.getCode()).getCheckedData();
        if (roleId == null) {
            throw exception(STUDENT_ROLE_NOT_EXISTS);
        }

        AdminUserCreateReqDTO userCreateReqDTO = new AdminUserCreateReqDTO();
        userCreateReqDTO.setUsername(student.getUsername());
        userCreateReqDTO.setNickname(student.getName());
        userCreateReqDTO.setMobile(StrUtil.blankToDefault(student.getMobile(), null));
        userCreateReqDTO.setSex(student.getSex());
        userCreateReqDTO.setRemark(student.getRemark());
        userCreateReqDTO.setPassword(initPassword);

        Long userId = adminUserApi.createUser(userCreateReqDTO).getCheckedData();
        permissionApi.assignUserRole(userId, Set.of(roleId)).checkError();
        return userId;
    }

    private void syncStudentToUser(StudentSaveReqVO student, Long userId) {
        AdminUserUpdateReqDTO userUpdateReqDTO = new AdminUserUpdateReqDTO();
        userUpdateReqDTO.setId(userId);
        // 登录名随档案一起同步
        userUpdateReqDTO.setUsername(student.getUsername());
        userUpdateReqDTO.setNickname(student.getName());
        userUpdateReqDTO.setMobile(StrUtil.blankToDefault(student.getMobile(), null));
        userUpdateReqDTO.setSex(student.getSex());
        userUpdateReqDTO.setRemark(student.getRemark());
        adminUserApi.updateUser(userUpdateReqDTO).checkError();
    }

    private String getInitPassword() {
        CommonResult<String> configResult = configApi.getConfigValueByKey(INIT_PASSWORD_KEY);
        if (configResult != null && configResult.isSuccess() && StrUtil.isNotBlank(configResult.getData())) {
            return configResult.getData();
        }
        return DEFAULT_INIT_PASSWORD;
    }

    private void validateUsernameFormat(String username) {
        if (StrUtil.isBlank(username) || !username.matches("^[a-zA-Z0-9]{4,30}$")) {
            throw exception(STUDENT_USERNAME_INVALID);
        }
    }

    private void validateUsernameUnique(Long id, String username) {
        StudentDO student = studentMapper.selectByUsername(username);
        if (student == null) {
            return;
        }
        if (id == null || !student.getId().equals(id)) {
            throw exception(STUDENT_USERNAME_EXISTS);
        }
    }

    private void validateStudentNoUnique(Long id, String studentNo) {
        StudentDO student = studentMapper.selectByStudentNo(studentNo);
        if (student == null) {
            return;
        }
        if (id == null || !student.getId().equals(id)) {
            throw exception(STUDENT_NO_EXISTS);
        }
    }

    /**
     * 生成学生账号：S0001、S0002…（至少 4 位补零，超过 9999 自然变长）
     * <p>
     * 以库中最大流水号 +1 为准，避免 Redis INCR 在失败重试时跳号。
     */
    private synchronized String generateStudentAccount() {
        long nextSeq = resolveMaxAccountSequenceFromDb() + 1;
        return formatStudentAccount(nextSeq);
    }

    private long resolveMaxAccountSequenceFromDb() {
        Long maxSeq = studentMapper.selectMaxAccountSequence();
        return maxSeq == null ? 0L : maxSeq;
    }

    private String formatStudentAccount(long sequenceNo) {
        return STUDENT_ACCOUNT_PREFIX + String.format("%04d", sequenceNo);
    }

}
