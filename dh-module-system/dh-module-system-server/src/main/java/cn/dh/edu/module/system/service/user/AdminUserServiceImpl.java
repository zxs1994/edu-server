package cn.dh.edu.module.system.service.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.dh.edu.framework.common.enums.CommonStatusEnum;
import cn.dh.edu.framework.common.enums.UserTypeEnum;
import cn.dh.edu.framework.common.exception.ServiceException;
import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.common.util.collection.CollectionUtils;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.framework.common.util.validation.ValidationUtils;
import cn.dh.edu.framework.datapermission.core.util.DataPermissionUtils;
import cn.dh.edu.module.infra.api.config.ConfigApi;
import cn.dh.edu.module.system.controller.admin.auth.vo.AuthRegisterReqVO;
import cn.dh.edu.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import cn.dh.edu.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import cn.dh.edu.module.system.controller.admin.user.vo.user.UserImportExcelVO;
import cn.dh.edu.module.system.controller.admin.user.vo.user.UserImportRespVO;
import cn.dh.edu.module.system.controller.admin.user.vo.user.UserPageReqVO;
import cn.dh.edu.module.system.controller.admin.user.vo.user.UserSaveReqVO;
import cn.dh.edu.module.system.dal.dataobject.dept.DeptDO;
import cn.dh.edu.module.system.dal.dataobject.dept.UserPostDO;
import cn.dh.edu.module.system.dal.dataobject.permission.RoleDO;
import cn.dh.edu.module.system.dal.dataobject.user.AdminUserDO;
import cn.dh.edu.module.system.dal.mysql.dept.UserPostMapper;
import cn.dh.edu.module.system.dal.mysql.user.AdminUserMapper;
import cn.dh.edu.module.hrm.api.employee.EmployeeApi;
import cn.dh.edu.module.hrm.api.employee.dto.EmployeeUpdateReqDTO;
import cn.dh.edu.module.edu.api.student.StudentApi;
import cn.dh.edu.module.edu.api.student.dto.StudentUpdateReqDTO;
import cn.dh.edu.module.edu.api.teacher.TeacherApi;
import cn.dh.edu.module.edu.api.teacher.dto.TeacherUpdateReqDTO;
import cn.dh.edu.module.system.service.dept.DeptService;
import cn.dh.edu.module.system.service.dept.PostService;
import cn.dh.edu.module.system.service.oauth2.OAuth2TokenService;
import cn.dh.edu.module.system.service.permission.PermissionService;
import cn.dh.edu.module.system.service.permission.RoleService;
import cn.dh.edu.module.system.service.tenant.TenantService;
import cn.dh.edu.module.system.enums.permission.RoleCodeEnum;
import com.google.common.annotations.VisibleForTesting;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.framework.common.util.collection.CollectionUtils.*;
import static cn.dh.edu.module.system.enums.ErrorCodeConstants.*;
import static cn.dh.edu.module.system.enums.LogRecordConstants.*;

/**
 * 后台用户 Service 实现类
 *
 * @author 鼎衡
 */
@Service("adminUserService")
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    static final String USER_INIT_PASSWORD_KEY = "system.user.init-password";

    static final String USER_REGISTER_ENABLED_KEY = "system.user.register-enabled";

    @Resource
    private AdminUserMapper userMapper;

    @Resource
    private DeptService deptService;
    @Resource
    private PostService postService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private RoleService roleService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private TenantService tenantService;
    @Resource
    @Lazy // 懒加载，避免循环依赖
    private OAuth2TokenService oauth2TokenService;

    @Resource
    private UserPostMapper userPostMapper;

    @Resource
    private ConfigApi configApi;

    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private EmployeeApi employeeApi;

    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private StudentApi studentApi;

    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private TeacherApi teacherApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_CREATE_SUB_TYPE, bizNo = "{{#user.id}}",
            success = SYSTEM_USER_CREATE_SUCCESS)
    public Long createUser(UserSaveReqVO createReqVO) {
        // 1.1 校验账户配合
        tenantService.handleTenantInfo(tenant -> {
            long count = userMapper.selectCount();
            if (count >= tenant.getAccountCount()) {
                throw exception(USER_COUNT_MAX, tenant.getAccountCount());
            }
        });
        // 1.2 校验正确性
        validateUserForCreateOrUpdate(null, createReqVO.getUsername(),
                createReqVO.getMobile(), createReqVO.getEmail(), createReqVO.getDeptId(), createReqVO.getPostIds());
        // 2.1 插入用户
        AdminUserDO user = BeanUtils.toBean(createReqVO, AdminUserDO.class);
        user.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认开启
        user.setPassword(encodePassword(createReqVO.getPassword())); // 加密密码
        userMapper.insert(user);
        // 2.2 插入关联岗位
        if (CollectionUtil.isNotEmpty(user.getPostIds())) {
            userPostMapper.insertBatch(convertList(user.getPostIds(),
                    postId -> new UserPostDO().setUserId(user.getId()).setPostId(postId)));
        }

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("user", user);
        return user.getId();
    }

    @Override
    public Long registerUser(AuthRegisterReqVO registerReqVO) {
        // 1.1 校验是否开启注册
        if (ObjUtil.notEqual(configApi.getConfigValueByKey(USER_REGISTER_ENABLED_KEY).getCheckedData(), "true")) {
            throw exception(USER_REGISTER_DISABLED);
        }
        // 1.2 校验账户配合
        tenantService.handleTenantInfo(tenant -> {
            long count = userMapper.selectCount();
            if (count >= tenant.getAccountCount()) {
                throw exception(USER_COUNT_MAX, tenant.getAccountCount());
            }
        });
        // 1.3 校验正确性
        validateUserForCreateOrUpdate(null, registerReqVO.getUsername(), null, null, null, null);

        // 2. 插入用户
        AdminUserDO user = BeanUtils.toBean(registerReqVO, AdminUserDO.class);
        user.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认开启
        user.setPassword(encodePassword(registerReqVO.getPassword())); // 加密密码
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = SYSTEM_USER_UPDATE_SUCCESS)
    public void updateUser(UserSaveReqVO updateReqVO) {
        updateReqVO.setPassword(null); // 特殊：此处不更新密码
        // 1. 校验正确性
        AdminUserDO oldUser = validateUserForCreateOrUpdate(updateReqVO.getId(), updateReqVO.getUsername(),
                updateReqVO.getMobile(), updateReqVO.getEmail(), updateReqVO.getDeptId(), updateReqVO.getPostIds());
        // 1.1 学生/教培账号：禁止修改账号，手机号必填
        validateFixedIdentityUserUpdate(updateReqVO.getId(), oldUser.getUsername(), updateReqVO);

        // 2.1 更新用户
        AdminUserDO updateObj = BeanUtils.toBean(updateReqVO, AdminUserDO.class);
        userMapper.updateById(updateObj);
        // 2.2 更新岗位
        updateUserPost(updateReqVO, updateObj);
        // 2.3 如果关联了员工，同步更新员工信息
        syncUserToEmployee(updateReqVO);
        // 2.4 如果关联了学生，同步更新学生档案
        syncUserToStudent(updateReqVO);
        // 2.5 如果关联了教培，同步更新教培档案
        syncUserToTeacher(updateReqVO);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldUser, UserSaveReqVO.class));
        LogRecordContext.putVariable("user", oldUser);
    }

    private void updateUserPost(UserSaveReqVO reqVO, AdminUserDO updateObj) {
        Long userId = reqVO.getId();
        Set<Long> dbPostIds = convertSet(userPostMapper.selectListByUserId(userId), UserPostDO::getPostId);
        // 计算新增和删除的岗位编号
        Set<Long> postIds = CollUtil.emptyIfNull(updateObj.getPostIds());
        Collection<Long> createPostIds = CollUtil.subtract(postIds, dbPostIds);
        Collection<Long> deletePostIds = CollUtil.subtract(dbPostIds, postIds);
        // 执行新增和删除。对于已经授权的岗位，不用做任何处理
        if (!CollectionUtil.isEmpty(createPostIds)) {
            userPostMapper.insertBatch(convertList(createPostIds,
                    postId -> new UserPostDO().setUserId(userId).setPostId(postId)));
        }
        if (!CollectionUtil.isEmpty(deletePostIds)) {
            userPostMapper.deleteByUserIdAndPostId(userId, deletePostIds);
        }
    }

    @Override
    public void updateUserLogin(Long id, String loginIp) {
        userMapper.updateById(new AdminUserDO().setId(id).setLoginIp(loginIp).setLoginDate(LocalDateTime.now()));
    }

    @Override
    public void updateUserProfile(Long id, UserProfileUpdateReqVO reqVO) {
        // 校验正确性
        validateUserExists(id);
        validateEmailUnique(id, reqVO.getEmail());
        validateMobileUnique(id, reqVO.getMobile());
        validateFixedIdentityMobile(id, reqVO.getMobile());
        // 只更新个人资料字段，严禁触碰 password / username / status 等
        AdminUserDO updateObj = new AdminUserDO();
        updateObj.setId(id);
        updateObj.setNickname(reqVO.getNickname());
        updateObj.setEmail(reqVO.getEmail());
        updateObj.setMobile(reqVO.getMobile());
        updateObj.setSex(reqVO.getSex());
        updateObj.setAvatar(reqVO.getAvatar());
        userMapper.updateById(updateObj);
        // 同步到学生档案
        syncProfileToStudent(id, reqVO);
        // 同步到教培档案
        syncProfileToTeacher(id, reqVO);
    }

    @Override
    public void updateUserPassword(Long id, UserProfileUpdatePasswordReqVO reqVO) {
        // 校验旧密码密码
        validateOldPassword(id, reqVO.getOldPassword());
        // 执行更新
        AdminUserDO updateObj = new AdminUserDO().setId(id);
        updateObj.setPassword(encodePassword(reqVO.getNewPassword())); // 加密密码
        userMapper.updateById(updateObj);
    }

    @Override
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_UPDATE_PASSWORD_SUB_TYPE, bizNo = "{{#id}}",
            success = SYSTEM_USER_UPDATE_PASSWORD_SUCCESS)
    public void updateUserPassword(Long id, String password) {
        // 1. 校验用户存在
        AdminUserDO user = validateUserExists(id);

        // 2. 更新密码
        AdminUserDO updateObj = new AdminUserDO();
        updateObj.setId(id);
        updateObj.setPassword(encodePassword(password)); // 加密密码
        userMapper.updateById(updateObj);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("user", user);
        LogRecordContext.putVariable("newPassword", updateObj.getPassword());
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        // 校验用户存在
        validateUserExists(id);
        // 禁止禁用超级管理员
        if (CommonStatusEnum.isDisable(status)) {
            validateSuperAdminNotDisable(id);
        }
        // 更新状态
        AdminUserDO updateObj = new AdminUserDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        userMapper.updateById(updateObj);

        // 如果是禁用用户，则删除其 Token 信息
        if (CommonStatusEnum.isDisable(status)) {
            oauth2TokenService.removeAccessToken(id, UserTypeEnum.ADMIN.getValue());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = SYSTEM_USER_DELETE_SUCCESS)
    public void deleteUser(Long id) {
        // 1. 校验用户存在
        AdminUserDO user = validateUserExists(id);
        // 1.1 禁止删除超级管理员
        validateSuperAdminNotDelete(id);

        // 2.1 删除用户
        userMapper.deleteById(id);
        // 2.2 删除用户关联数据
        permissionService.processUserDeleted(id);
        // 2.2 删除用户岗位
        userPostMapper.deleteByUserId(id);
        // 2.3 更新关联员工的用户生成标记
        updateEmployeeUserGeneratedStatus(id, false);
        // 2.4 同步删除学生档案
        deleteStudentByUserId(id);
        // 2.5 同步删除教培档案
        deleteTeacherByUserId(id);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("user", user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserList(List<Long> ids) {
        // 1. 校验：禁止删除超级管理员
        ids.forEach(this::validateSuperAdminNotDelete);

        // 2. 批量删除用户
        userMapper.deleteByIds(ids);

        // 3. 批量删除用户关联数据
        ids.forEach(id -> {
            permissionService.processUserDeleted(id);
            userPostMapper.deleteByUserId(id);
            // 更新关联员工的用户生成标记
            updateEmployeeUserGeneratedStatus(id, false);
            // 同步删除学生档案
            deleteStudentByUserId(id);
            // 同步删除教培档案
            deleteTeacherByUserId(id);
        });
    }

    /**
     * 校验目标用户不是超级管理员（删除场景）
     */
    private void validateSuperAdminNotDelete(Long id) {
        if (isSuperAdminUser(id)) {
            throw exception(USER_CAN_NOT_DELETE_ADMIN);
        }
    }

    /**
     * 校验目标用户不是超级管理员（禁用场景）
     */
    private void validateSuperAdminNotDisable(Long id) {
        if (isSuperAdminUser(id)) {
            throw exception(USER_CAN_NOT_DISABLE_ADMIN);
        }
    }

    private boolean isSuperAdminUser(Long userId) {
        Set<Long> roleIds = permissionService.getUserRoleIdListByUserId(userId);
        return roleService.hasAnySuperAdmin(roleIds);
    }

    @Override
    public AdminUserDO getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public AdminUserDO getUserByMobile(String mobile) {
        return userMapper.selectByMobile(mobile);
    }

    @Override
    public PageResult<AdminUserDO> getUserPage(UserPageReqVO reqVO) {
        // 如果有角色编号，查询角色对应的用户编号
        Set<Long> userIds = null;
        if (reqVO.getRoleId() != null) {
            userIds = permissionService.getUserRoleIdListByRoleId(singleton(reqVO.getRoleId()));
            if (CollUtil.isEmpty(userIds)) {
                return PageResult.empty();
            }
        }
        if (StrUtil.isNotBlank(reqVO.getIncludeRoleCodes())) {
            Set<Long> roleIds = new HashSet<>();
            for (String roleCode : reqVO.getIncludeRoleCodes().split(",")) {
                if (StrUtil.isBlank(roleCode)) {
                    continue;
                }
                RoleDO role = roleService.getRoleByCode(roleCode.trim());
                if (role != null) {
                    roleIds.add(role.getId());
                }
            }
            if (CollUtil.isEmpty(roleIds)) {
                return PageResult.empty();
            }
            Set<Long> includeUserIds = permissionService.getUserRoleIdListByRoleId(roleIds);
            if (CollUtil.isEmpty(includeUserIds)) {
                return PageResult.empty();
            }
            if (userIds == null) {
                userIds = includeUserIds;
            } else {
                userIds.retainAll(includeUserIds);
                if (CollUtil.isEmpty(userIds)) {
                    return PageResult.empty();
                }
            }
        }
        if (CollUtil.isNotEmpty(reqVO.getUserIds())) {
            Set<Long> reqUserIds = new HashSet<>(reqVO.getUserIds());
            if (userIds == null) {
                userIds = reqUserIds;
            } else {
                userIds.retainAll(reqUserIds);
                if (CollUtil.isEmpty(userIds)) {
                    return PageResult.empty();
                }
            }
        }

        // 分页查询
        return userMapper.selectPage(reqVO, getDeptCondition(reqVO.getDeptId()), userIds);
    }

    @Override
    public AdminUserDO getUser(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<AdminUserDO> getUserListByDeptIds(Collection<Long> deptIds) {
        if (CollUtil.isEmpty(deptIds)) {
            return Collections.emptyList();
        }
        return userMapper.selectListByDeptIds(deptIds);
    }

    @Override
    public List<AdminUserDO> getUserListByPostIds(Collection<Long> postIds) {
        if (CollUtil.isEmpty(postIds)) {
            return Collections.emptyList();
        }
        Set<Long> userIds = convertSet(userPostMapper.selectListByPostIds(postIds), UserPostDO::getUserId);
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return userMapper.selectByIds(userIds);
    }

    @Override
    public List<AdminUserDO> getUserList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return userMapper.selectByIds(ids);
    }

    @Override
    public void validateUserList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得岗位信息
        List<AdminUserDO> users = userMapper.selectByIds(ids);
        Map<Long, AdminUserDO> userMap = CollectionUtils.convertMap(users, AdminUserDO::getId);
        // 校验
        ids.forEach(id -> {
            AdminUserDO user = userMap.get(id);
            if (user == null) {
                throw exception(USER_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(user.getStatus())) {
                throw exception(USER_IS_DISABLE, user.getNickname());
            }
        });
    }

    @Override
    public List<AdminUserDO> getUserListByNickname(String nickname) {
        return userMapper.selectListByNickname(nickname);
    }

    /**
     * 获得部门条件：查询指定部门的子部门编号们，包括自身
     *
     * @param deptId 部门编号
     * @return 部门编号集合
     */
    private Set<Long> getDeptCondition(Long deptId) {
        if (deptId == null) {
            return Collections.emptySet();
        }
        Set<Long> deptIds = convertSet(deptService.getChildDeptList(deptId), DeptDO::getId);
        deptIds.add(deptId); // 包括自身
        return deptIds;
    }

    private AdminUserDO validateUserForCreateOrUpdate(Long id, String username, String mobile, String email,
                                               Long deptId, Set<Long> postIds) {
        // 关闭数据权限，避免因为没有数据权限，查询不到数据，进而导致唯一校验不正确
        return DataPermissionUtils.executeIgnore(() -> {
            // 校验用户存在
            AdminUserDO user = validateUserExists(id);
            // 校验用户账号唯一
            validateUsernameUnique(id, username);
            // 校验手机号唯一
            validateMobileUnique(id, mobile);
            // 校验邮箱唯一
            validateEmailUnique(id, email);
            // 校验部门处于开启状态
            deptService.validateDeptList(CollectionUtils.singleton(deptId));
            // 校验岗位处于开启状态
            postService.validatePostList(postIds);
            return user;
        });
    }

    @VisibleForTesting
    AdminUserDO validateUserExists(Long id) {
        if (id == null) {
            return null;
        }
        AdminUserDO user = userMapper.selectById(id);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        return user;
    }

    @VisibleForTesting
    void validateUsernameUnique(Long id, String username) {
        if (StrUtil.isBlank(username)) {
            return;
        }
        AdminUserDO user = userMapper.selectByUsername(username);
        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(USER_USERNAME_EXISTS);
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_USERNAME_EXISTS);
        }
    }

    @VisibleForTesting
    void validateEmailUnique(Long id, String email) {
        if (StrUtil.isBlank(email)) {
            return;
        }
        AdminUserDO user = userMapper.selectByEmail(email);
        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(USER_EMAIL_EXISTS);
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_EMAIL_EXISTS);
        }
    }

    @VisibleForTesting
    void validateMobileUnique(Long id, String mobile) {
        if (StrUtil.isBlank(mobile)) {
            return;
        }
        AdminUserDO user = userMapper.selectByMobile(mobile);
        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(USER_MOBILE_EXISTS);
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_MOBILE_EXISTS);
        }
    }

    /**
     * 校验旧密码
     * @param id          用户 id
     * @param oldPassword 旧密码
     */
    @VisibleForTesting
    void validateOldPassword(Long id, String oldPassword) {
        AdminUserDO user = userMapper.selectById(id);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        if (!isPasswordMatch(oldPassword, user.getPassword())) {
            throw exception(USER_PASSWORD_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 添加事务，异常则回滚所有导入
    public UserImportRespVO importUserList(List<UserImportExcelVO> importUsers, boolean isUpdateSupport) {
        // 1.1 参数校验
        if (CollUtil.isEmpty(importUsers)) {
            throw exception(USER_IMPORT_LIST_IS_EMPTY);
        }
        // 1.2 初始化密码不能为空
        String initPassword = configApi.getConfigValueByKey(USER_INIT_PASSWORD_KEY).getCheckedData();
        if (StrUtil.isEmpty(initPassword)) {
            throw exception(USER_IMPORT_INIT_PASSWORD);
        }

        // 2. 遍历，逐个创建 or 更新
        UserImportRespVO respVO = UserImportRespVO.builder().createUsernames(new ArrayList<>())
                .updateUsernames(new ArrayList<>()).failureUsernames(new LinkedHashMap<>()).build();
        AtomicInteger index = new AtomicInteger(1);
        importUsers.forEach(importUser -> {
            int currentIndex = index.getAndIncrement();
            // 2.1.1 校验字段是否符合要求
            try {
                ValidationUtils.validate(BeanUtils.toBean(importUser, UserSaveReqVO.class).setPassword(initPassword));
            } catch (ConstraintViolationException ex) {
                String key = StrUtil.blankToDefault(importUser.getUsername(), "第 " + currentIndex + " 行");
                respVO.getFailureUsernames().put(key, ex.getMessage());
                return;
            }
            // 2.1.2 校验，判断是否有不符合的原因
            try {
                validateUserForCreateOrUpdate(null, null, importUser.getMobile(), importUser.getEmail(),
                        importUser.getDeptId(), null);
            } catch (ServiceException ex) {
                respVO.getFailureUsernames().put(importUser.getUsername(), ex.getMessage());
                return;
            }

            // 2.2.1 判断如果不存在，在进行插入
            AdminUserDO existUser = userMapper.selectByUsername(importUser.getUsername());
            if (existUser == null) {
                userMapper.insert(BeanUtils.toBean(importUser, AdminUserDO.class)
                        .setPassword(encodePassword(initPassword)).setPostIds(new HashSet<>())); // 设置默认密码及空岗位编号数组
                respVO.getCreateUsernames().add(importUser.getUsername());
                return;
            }
            // 2.2.2 如果存在，判断是否允许更新
            if (!isUpdateSupport) {
                respVO.getFailureUsernames().put(importUser.getUsername(), USER_USERNAME_EXISTS.getMsg());
                return;
            }
            AdminUserDO updateUser = BeanUtils.toBean(importUser, AdminUserDO.class);
            updateUser.setId(existUser.getId());
            userMapper.updateById(updateUser);
            respVO.getUpdateUsernames().add(importUser.getUsername());
        });
        return respVO;
    }

    @Override
    public List<AdminUserDO> getUserListByStatus(Integer status) {
        return userMapper.selectListByStatus(status);
    }

    @Override
    public List<AdminUserDO> getUserListByStatusExcludeRole(Integer status, String excludeRoleCode) {
        List<AdminUserDO> users = getUserListByStatus(status);
        if (CollUtil.isEmpty(users) || StrUtil.isBlank(excludeRoleCode)) {
            return users;
        }
        RoleDO role = roleService.getRoleByCode(excludeRoleCode);
        if (role == null) {
            return users;
        }
        Set<Long> excludeUserIds = permissionService.getUserRoleIdListByRoleId(singleton(role.getId()));
        if (CollUtil.isEmpty(excludeUserIds)) {
            return users;
        }
        return users.stream()
                .filter(user -> !excludeUserIds.contains(user.getId()))
                .toList();
    }

    @Override
    public List<AdminUserDO> getUserListByStatusIncludeRoles(Integer status, Collection<String> includeRoleCodes) {
        List<AdminUserDO> users = getUserListByStatus(status);
        if (CollUtil.isEmpty(users) || CollUtil.isEmpty(includeRoleCodes)) {
            return users;
        }
        Set<Long> roleIds = new HashSet<>();
        for (String roleCode : includeRoleCodes) {
            if (StrUtil.isBlank(roleCode)) {
                continue;
            }
            RoleDO role = roleService.getRoleByCode(roleCode.trim());
            if (role != null) {
                roleIds.add(role.getId());
            }
        }
        if (CollUtil.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        Set<Long> includeUserIds = permissionService.getUserRoleIdListByRoleId(roleIds);
        if (CollUtil.isEmpty(includeUserIds)) {
            return Collections.emptyList();
        }
        return users.stream()
                .filter(user -> includeUserIds.contains(user.getId()))
                .toList();
    }

    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 对密码进行加密
     *
     * @param password 密码
     * @return 加密后的密码
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 更新员工的用户生成标记
     *
     * @param userId 用户ID
     * @param userGenerated 是否已生成用户
     */
    private void updateEmployeeUserGeneratedStatus(Long userId, Boolean userGenerated) {
        if (userId == null) {
            return;
        }
        employeeApi.updateUserGeneratedStatus(userId, userGenerated);
    }

    /**
     * 同步用户信息到员工
     *
     * @param userUpdateReqVO 用户更新信息
     */
    private void syncUserToEmployee(UserSaveReqVO userUpdateReqVO) {
        if (userUpdateReqVO.getId() == null) {
            return;
        }
        EmployeeUpdateReqDTO employeeUpdateReqDTO = new EmployeeUpdateReqDTO();
        employeeUpdateReqDTO.setName(userUpdateReqVO.getNickname()); // 员工姓名为用户昵称
        employeeUpdateReqDTO.setMobile(userUpdateReqVO.getMobile()); // 手机号
        employeeUpdateReqDTO.setEmail(userUpdateReqVO.getEmail()); // 邮箱
        employeeUpdateReqDTO.setSex(userUpdateReqVO.getSex()); // 性别
        employeeUpdateReqDTO.setAvatar(userUpdateReqVO.getAvatar()); // 头像
        employeeUpdateReqDTO.setDeptId(userUpdateReqVO.getDeptId()); // 部门ID
        employeeUpdateReqDTO.setRemark(userUpdateReqVO.getRemark()); // 备注

        employeeApi.updateEmployeeByUserId(userUpdateReqVO.getId(), employeeUpdateReqDTO);
    }

    /**
     * 同步用户信息到学生档案
     */
    private void syncUserToStudent(UserSaveReqVO userUpdateReqVO) {
        if (userUpdateReqVO.getId() == null) {
            return;
        }
        StudentUpdateReqDTO studentUpdateReqDTO = new StudentUpdateReqDTO();
        studentUpdateReqDTO.setName(userUpdateReqVO.getNickname());
        studentUpdateReqDTO.setMobile(userUpdateReqVO.getMobile());
        studentUpdateReqDTO.setSex(userUpdateReqVO.getSex());
        studentUpdateReqDTO.setRemark(userUpdateReqVO.getRemark());
        studentApi.updateStudentByUserId(userUpdateReqVO.getId(), studentUpdateReqDTO);
    }

    /**
     * 同步用户信息到教培档案
     */
    private void syncUserToTeacher(UserSaveReqVO userUpdateReqVO) {
        if (userUpdateReqVO.getId() == null) {
            return;
        }
        TeacherUpdateReqDTO teacherUpdateReqDTO = new TeacherUpdateReqDTO();
        teacherUpdateReqDTO.setName(userUpdateReqVO.getNickname());
        teacherUpdateReqDTO.setMobile(userUpdateReqVO.getMobile());
        teacherUpdateReqDTO.setSex(userUpdateReqVO.getSex());
        teacherUpdateReqDTO.setRemark(userUpdateReqVO.getRemark());
        teacherApi.updateTeacherByUserId(userUpdateReqVO.getId(), teacherUpdateReqDTO);
    }

    /**
     * 同步个人中心资料到学生档案
     */
    private void syncProfileToStudent(Long userId, UserProfileUpdateReqVO reqVO) {
        if (userId == null) {
            return;
        }
        StudentUpdateReqDTO studentUpdateReqDTO = new StudentUpdateReqDTO();
        studentUpdateReqDTO.setName(reqVO.getNickname());
        studentUpdateReqDTO.setMobile(reqVO.getMobile());
        studentUpdateReqDTO.setSex(reqVO.getSex());
        studentApi.updateStudentByUserId(userId, studentUpdateReqDTO);
    }

    /**
     * 同步个人中心资料到教培档案
     */
    private void syncProfileToTeacher(Long userId, UserProfileUpdateReqVO reqVO) {
        if (userId == null) {
            return;
        }
        TeacherUpdateReqDTO teacherUpdateReqDTO = new TeacherUpdateReqDTO();
        teacherUpdateReqDTO.setName(reqVO.getNickname());
        teacherUpdateReqDTO.setMobile(reqVO.getMobile());
        teacherUpdateReqDTO.setSex(reqVO.getSex());
        teacherApi.updateTeacherByUserId(userId, teacherUpdateReqDTO);
    }

    /**
     * 根据用户删除学生档案
     */
    private void deleteStudentByUserId(Long userId) {
        if (userId == null) {
            return;
        }
        studentApi.deleteStudentByUserId(userId);
    }

    /**
     * 根据用户删除教培档案
     */
    private void deleteTeacherByUserId(Long userId) {
        if (userId == null) {
            return;
        }
        teacherApi.deleteTeacherByUserId(userId);
    }

    /**
     * 是否为学生/教培身份账号
     */
    private boolean isFixedIdentityUser(Long userId) {
        if (userId == null) {
            return false;
        }
        Set<Long> roleIds = permissionService.getUserRoleIdListByUserId(userId);
        if (CollUtil.isEmpty(roleIds)) {
            return false;
        }
        return roleService.getRoleList(roleIds).stream()
                .map(RoleDO::getCode)
                .anyMatch(RoleCodeEnum::isFixedIdentityRole);
    }

    /**
     * 学生/教培账号更新校验：禁止改账号，手机号必填
     */
    private void validateFixedIdentityUserUpdate(Long userId, String oldUsername, UserSaveReqVO updateReqVO) {
        if (!isFixedIdentityUser(userId)) {
            return;
        }
        if (!Objects.equals(oldUsername, updateReqVO.getUsername())) {
            throw exception(USER_USERNAME_UPDATE_FORBIDDEN);
        }
        validateFixedIdentityMobile(userId, updateReqVO.getMobile());
    }

    /**
     * 学生/教培账号手机号必填
     */
    private void validateFixedIdentityMobile(Long userId, String mobile) {
        if (!isFixedIdentityUser(userId)) {
            return;
        }
        if (StrUtil.isBlank(mobile)) {
            throw exception(USER_MOBILE_REQUIRED);
        }
    }

}
