package cn.dh.edu.module.system.api.permission;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.module.system.dal.dataobject.permission.RoleDO;
import cn.dh.edu.module.system.service.permission.RoleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Collection;

import static cn.dh.edu.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class RoleApiImpl implements RoleApi {

    @Resource
    private RoleService roleService;

    @Override
    public CommonResult<Boolean> validRoleList(Collection<Long> ids) {
        roleService.validateRoleList(ids);
        return success(true);
    }

    @Override
    public CommonResult<Long> getRoleIdByCode(String code) {
        RoleDO role = roleService.getRoleByCode(code);
        return success(role != null ? role.getId() : null);
    }
}
