package cn.dh.edu.module.edu.service.teacher;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.module.edu.controller.admin.teacher.vo.TeacherCreateRespVO;
import cn.dh.edu.module.edu.controller.admin.teacher.vo.TeacherPageReqVO;
import cn.dh.edu.module.edu.controller.admin.teacher.vo.TeacherRespVO;
import cn.dh.edu.module.edu.controller.admin.teacher.vo.TeacherSaveReqVO;
import cn.dh.edu.module.edu.dal.dataobject.teacher.TeacherDO;
import cn.dh.edu.module.edu.dal.mysql.teacher.TeacherMapper;
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
 * 教培档案 Service 实现类
 *
 * @author 鼎衡
 */
@Service
@Validated
public class TeacherServiceImpl implements TeacherService {

    private static final String INIT_PASSWORD_KEY = "system.user.init-password";
    private static final String DEFAULT_INIT_PASSWORD = "123456";
    /** 教培账号：T0001 起，至少 4 位数字补零 */
    private static final String TEACHER_ACCOUNT_PREFIX = "T";

    @Resource
    private TeacherMapper teacherMapper;
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
    public TeacherCreateRespVO createTeacher(TeacherSaveReqVO createReqVO) {
        createReqVO.setUsername(generateTeacherAccount());
        validateUsernameFormat(createReqVO.getUsername());
        validateUsernameUnique(null, createReqVO.getUsername());

        TeacherDO teacher = BeanUtils.toBean(createReqVO, TeacherDO.class);
        teacherMapper.insert(teacher);

        String initPassword = getInitPassword();
        Long userId = createTeacherUser(createReqVO, initPassword);
        TeacherDO updateObj = new TeacherDO();
        updateObj.setId(teacher.getId());
        updateObj.setUserId(userId);
        updateObj.setUserGenerated(true);
        teacherMapper.updateById(updateObj);

        return new TeacherCreateRespVO(teacher.getId(), createReqVO.getUsername(), initPassword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTeacher(TeacherSaveReqVO updateReqVO) {
        TeacherDO oldTeacher = teacherMapper.selectById(updateReqVO.getId());
        if (oldTeacher == null) {
            throw exception(TEACHER_NOT_EXISTS);
        }
        // 登录账号创建后不可修改
        updateReqVO.setUsername(oldTeacher.getUsername());

        TeacherDO updateObj = BeanUtils.toBean(updateReqVO, TeacherDO.class);
        teacherMapper.updateById(updateObj);

        if (Boolean.TRUE.equals(oldTeacher.getUserGenerated()) && oldTeacher.getUserId() != null) {
            syncTeacherToUser(updateReqVO, oldTeacher.getUserId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeacher(Long id) {
        TeacherDO teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw exception(TEACHER_NOT_EXISTS);
        }
        if (Boolean.TRUE.equals(teacher.getUserGenerated()) && teacher.getUserId() != null) {
            adminUserApi.deleteUser(teacher.getUserId());
            return;
        }
        teacherMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeacherList(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<TeacherDO> teachers = teacherMapper.selectBatchIds(ids);
        List<Long> userIds = teachers.stream()
                .filter(t -> Boolean.TRUE.equals(t.getUserGenerated()) && t.getUserId() != null)
                .map(TeacherDO::getUserId)
                .toList();
        if (CollUtil.isNotEmpty(userIds)) {
            adminUserApi.deleteUserList(userIds);
        }
        List<Long> archiveOnlyIds = teachers.stream()
                .filter(t -> !(Boolean.TRUE.equals(t.getUserGenerated()) && t.getUserId() != null))
                .map(TeacherDO::getId)
                .toList();
        if (CollUtil.isNotEmpty(archiveOnlyIds)) {
            teacherMapper.deleteByIds(archiveOnlyIds);
        }
    }

    @Override
    public TeacherRespVO getTeacher(Long id) {
        TeacherDO teacher = teacherMapper.selectById(id);
        return BeanUtils.toBean(teacher, TeacherRespVO.class);
    }

    @Override
    public TeacherRespVO getTeacherByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        TeacherDO teacher = teacherMapper.selectByUserId(userId);
        return BeanUtils.toBean(teacher, TeacherRespVO.class);
    }

    @Override
    public PageResult<TeacherRespVO> getTeacherPage(TeacherPageReqVO pageReqVO) {
        PageResult<TeacherDO> pageResult = teacherMapper.selectPage(pageReqVO);
        return BeanUtils.toBean(pageResult, TeacherRespVO.class);
    }

    private Long createTeacherUser(TeacherSaveReqVO teacher, String initPassword) {
        Long roleId = roleApi.getRoleIdByCode(RoleCodeEnum.TEACHER.getCode()).getCheckedData();
        if (roleId == null) {
            throw exception(TEACHER_ROLE_NOT_EXISTS);
        }

        AdminUserCreateReqDTO userCreateReqDTO = new AdminUserCreateReqDTO();
        userCreateReqDTO.setUsername(teacher.getUsername());
        userCreateReqDTO.setNickname(teacher.getName());
        userCreateReqDTO.setMobile(StrUtil.blankToDefault(teacher.getMobile(), null));
        userCreateReqDTO.setSex(teacher.getSex());
        userCreateReqDTO.setRemark(teacher.getRemark());
        userCreateReqDTO.setPassword(initPassword);

        Long userId = adminUserApi.createUser(userCreateReqDTO).getCheckedData();
        permissionApi.assignUserRole(userId, Set.of(roleId)).checkError();
        return userId;
    }

    private void syncTeacherToUser(TeacherSaveReqVO teacher, Long userId) {
        AdminUserUpdateReqDTO userUpdateReqDTO = new AdminUserUpdateReqDTO();
        userUpdateReqDTO.setId(userId);
        userUpdateReqDTO.setUsername(teacher.getUsername());
        userUpdateReqDTO.setNickname(teacher.getName());
        userUpdateReqDTO.setMobile(StrUtil.blankToDefault(teacher.getMobile(), null));
        userUpdateReqDTO.setSex(teacher.getSex());
        userUpdateReqDTO.setRemark(teacher.getRemark());
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
            throw exception(TEACHER_USERNAME_INVALID);
        }
    }

    private void validateUsernameUnique(Long id, String username) {
        TeacherDO teacher = teacherMapper.selectByUsername(username);
        if (teacher == null) {
            return;
        }
        if (id == null || !teacher.getId().equals(id)) {
            throw exception(TEACHER_USERNAME_EXISTS);
        }
    }

    /**
     * 生成教培账号：T0001、T0002…（至少 4 位补零，超过 9999 自然变长）
     */
    private synchronized String generateTeacherAccount() {
        long nextSeq = resolveMaxAccountSequenceFromDb() + 1;
        return formatTeacherAccount(nextSeq);
    }

    private long resolveMaxAccountSequenceFromDb() {
        Long maxSeq = teacherMapper.selectMaxAccountSequence();
        return maxSeq == null ? 0L : maxSeq;
    }

    private String formatTeacherAccount(long sequenceNo) {
        return TEACHER_ACCOUNT_PREFIX + String.format("%04d", sequenceNo);
    }

}
