package cn.dh.edu.module.bpm.util;

import cn.dh.edu.module.bpm.api.task.BpmProcessInstanceApi;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 删除业务单据时取消对应 BPM 流程，从而清掉运行中待办
 */
@Slf4j
public final class BpmProcessInstanceCancelUtils {

    public static final String REASON_BILL_DELETED = "单据已删除";

    private BpmProcessInstanceCancelUtils() {
    }

    public static void cancelIfExists(BpmProcessInstanceApi api, String processInstanceId) {
        if (api == null || StrUtil.isBlank(processInstanceId)) {
            return;
        }
        try {
            api.cancelProcessInstanceByReason(processInstanceId, REASON_BILL_DELETED);
        } catch (Exception ex) {
            log.warn("[cancelIfExists] 取消流程失败，processInstanceId={}", processInstanceId, ex);
        }
        try {
            api.deleteProcessInstanceCopy(processInstanceId);
        } catch (Exception ex) {
            log.warn("[cancelIfExists] 删除抄送失败，processInstanceId={}", processInstanceId, ex);
        }
    }

}
