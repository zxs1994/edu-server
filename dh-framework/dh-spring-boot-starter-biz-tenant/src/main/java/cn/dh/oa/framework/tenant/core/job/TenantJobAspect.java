package cn.dh.oa.framework.tenant.core.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.dh.oa.framework.common.util.json.JsonUtils;
import cn.dh.oa.framework.tenant.core.service.TenantFrameworkService;
import cn.dh.oa.framework.tenant.core.util.TenantUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 多租户 JobHandler AOP
 * 任务执行时，会按照租户逐个执行 Job 的逻辑
 *
 * 注意，需要保证 JobHandler 的幂等性。因为 Job 因为某个租户执行失败重试时，之前执行成功的租户也会再次执行。
 *
 * @author 鼎衡
 */
@Aspect
@RequiredArgsConstructor
@Slf4j
public class TenantJobAspect {

    private final TenantFrameworkService tenantFrameworkService;

    @Around("@annotation(tenantJob)")
    public void around(ProceedingJoinPoint joinPoint, TenantJob tenantJob) {
        // 获得租户列表
        List<Long> tenantIds = tenantFrameworkService.getTenantIds();
        if (CollUtil.isEmpty(tenantIds)) {
            return;
        }

        // 逐个租户，执行 Job
        Map<Long, String> results = new ConcurrentHashMap<>();
        AtomicBoolean success = new AtomicBoolean(true); // 标记，是否存在失败的情况
        tenantIds.parallelStream().forEach(tenantId -> {
            // TODO 芋艿：先通过 parallel 实现并行；1）多个租户，是一条执行日志；2）异常的情况
            TenantUtils.execute(tenantId, () -> {
                try {
                    // 执行 Job
                    Object result = joinPoint.proceed();
                    results.put(tenantId, result != null ? result.toString() : "");
                } catch (Throwable e) {
                    results.put(tenantId, ExceptionUtil.getRootCauseMessage(e));
                    success.set(false);
                    // 打印异常
                    log.error("[多租户({}) 执行任务({})，发生异常]", tenantId, joinPoint.getSignature(), e);
                }
            });
        });
        // 记录执行结果
        if (success.get()) {
            log.info("[任务({}) 执行成功，结果：{}]", joinPoint.getSignature(), JsonUtils.toJsonString(results));
        } else {
            log.error("[任务({}) 执行失败，结果：{}]", joinPoint.getSignature(), JsonUtils.toJsonString(results));
        }
    }

}
