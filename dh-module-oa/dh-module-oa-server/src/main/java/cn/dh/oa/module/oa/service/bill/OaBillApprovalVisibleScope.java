package cn.dh.oa.module.oa.service.bill;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Data;

import java.util.Collection;
import java.util.Collections;

/**
 * 审批管理单据可见范围条件
 */
@Data
public class OaBillApprovalVisibleScope {

    /** 可查看全部时为 true，此时不追加 creator/待办条件 */
    private boolean viewAll;

    /** 当前用户 ID 字符串，对应单据 creator */
    private String creator;

    /** 当前用户待办流程实例 ID */
    private Collection<String> processInstanceIds;

    public static OaBillApprovalVisibleScope viewAll() {
        OaBillApprovalVisibleScope scope = new OaBillApprovalVisibleScope();
        scope.setViewAll(true);
        scope.setProcessInstanceIds(Collections.emptyList());
        return scope;
    }

    public static OaBillApprovalVisibleScope of(String creator, Collection<String> processInstanceIds) {
        OaBillApprovalVisibleScope scope = new OaBillApprovalVisibleScope();
        scope.setViewAll(false);
        scope.setCreator(creator);
        scope.setProcessInstanceIds(processInstanceIds != null ? processInstanceIds : Collections.emptyList());
        return scope;
    }

    /**
     * 追加「本人发起 OR 待我审批」条件；viewAll 时不处理
     */
    public <T> void apply(LambdaQueryWrapper<T> wrapper,
                          SFunction<T, ?> creatorGetter,
                          SFunction<T, ?> processInstanceIdGetter) {
        if (viewAll || wrapper == null) {
            return;
        }
        wrapper.and(w -> {
            w.eq(creatorGetter, creator);
            if (CollUtil.isNotEmpty(processInstanceIds)) {
                w.or().in(processInstanceIdGetter, processInstanceIds);
            }
        });
    }

    public boolean canView(String billCreator, String processInstanceId) {
        if (viewAll) {
            return true;
        }
        if (StrUtil.isNotBlank(creator) && StrUtil.equals(creator, billCreator)) {
            return true;
        }
        return StrUtil.isNotBlank(processInstanceId)
                && CollUtil.isNotEmpty(processInstanceIds)
                && processInstanceIds.contains(processInstanceId);
    }

}
