package cn.dh.edu.module.infra.job.logger;

import cn.dh.edu.framework.tenant.core.aop.TenantIgnore;
import cn.dh.edu.module.infra.service.logger.ApiErrorLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 物理删除 N 天前的错误日志的 Job
 *
 * @author j-sentinel
 */
@Slf4j
@Component
public class ErrorLogCleanJob {

    @Resource
    private ApiErrorLogService apiErrorLogService;

    /**
     * 清理超过（14）天的日志
     */
    private static final Integer JOB_CLEAN_RETAIN_DAY = 14;

    /**
     * 每次删除间隔的条数，如果值太高可能会造成数据库的压力过大
     */
    private static final Integer DELETE_LIMIT = 100;

    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨 2 点执行
    @TenantIgnore
    public void execute() {
        Integer count = apiErrorLogService.cleanErrorLog(JOB_CLEAN_RETAIN_DAY,DELETE_LIMIT);
        log.info("[execute][定时执行清理错误日志数量 ({}) 个]", count);
    }

}
