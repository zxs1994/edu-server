package cn.dh.oa.module.infra.job.logger;

import cn.dh.oa.framework.tenant.core.aop.TenantIgnore;
import cn.dh.oa.module.infra.service.logger.ApiAccessLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 物理删除 N 天前的访问日志的 Job
 *
 * @author j-sentinel
 */
@Component
@Slf4j
public class AccessLogCleanJob {

    @Resource
    private ApiAccessLogService apiAccessLogService;

    /**
     * 清理超过（14）天的日志
     */
    private static final Integer JOB_CLEAN_RETAIN_DAY = 14;

    /**
     * 每次删除间隔的条数，如果值太高可能会造成数据库的压力过大
     */
    private static final Integer DELETE_LIMIT = 100;

    @Scheduled(cron = "0 30 2 * * ?") // 每天凌晨 2:30 执行，错开与 ErrorLogCleanJob 的时间
    @TenantIgnore
    public void execute() {
        Integer count = apiAccessLogService.cleanAccessLog(JOB_CLEAN_RETAIN_DAY, DELETE_LIMIT);
        log.info("[execute][定时执行清理访问日志数量 ({}) 个]", count);
    }

}
