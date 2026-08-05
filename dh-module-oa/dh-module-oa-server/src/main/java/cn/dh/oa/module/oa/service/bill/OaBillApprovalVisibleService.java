package cn.dh.oa.module.oa.service.bill;

import cn.dh.oa.framework.security.core.service.SecurityFrameworkService;
import cn.dh.oa.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.oa.module.bpm.api.task.BpmTaskApi;
import cn.dh.oa.module.system.enums.permission.RoleCodeEnum;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.OA_BILL_VIEW_DENIED;

/**
 * 审批管理单据可见范围：
 * <ul>
 *   <li>列表：超管/综合管理员看全部；否则本人发起 + 当前待我审批</li>
 *   <li>详情：在列表范围基础上，额外允许「我作为办理人参与过」的流程回看</li>
 * </ul>
 */
@Service
public class OaBillApprovalVisibleService {

    @Resource
    private SecurityFrameworkService securityFrameworkService;
    @Resource
    private BpmTaskApi bpmTaskApi;

    public boolean canViewAll() {
        return securityFrameworkService.hasAnyRoles(
                RoleCodeEnum.SUPER_ADMIN.getCode(),
                RoleCodeEnum.OA_COMPREHENSIVE_ADMIN.getCode());
    }

    public OaBillApprovalVisibleScope resolveCurrentUserScope() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            return OaBillApprovalVisibleScope.of("", Collections.emptyList());
        }
        if (canViewAll()) {
            return OaBillApprovalVisibleScope.viewAll();
        }
        List<String> todoIds = bpmTaskApi.getTodoProcessInstanceIds(userId).getCheckedData();
        return OaBillApprovalVisibleScope.of(String.valueOf(userId),
                todoIds != null ? todoIds : Collections.emptyList());
    }

    public void assertCanView(String creator, String processInstanceId) {
        OaBillApprovalVisibleScope scope = resolveCurrentUserScope();
        if (scope.canView(creator, processInstanceId)) {
            return;
        }
        // 已办回看：参与过审批即可看详情，不把历史已办并入列表 scope
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId != null && StrUtil.isNotBlank(processInstanceId)
                && Boolean.TRUE.equals(bpmTaskApi.isUserTaskParticipant(userId, processInstanceId).getCheckedData())) {
            return;
        }
        throw exception(OA_BILL_VIEW_DENIED);
    }

}
