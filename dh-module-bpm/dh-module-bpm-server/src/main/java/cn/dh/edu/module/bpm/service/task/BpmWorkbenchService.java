package cn.dh.edu.module.bpm.service.task;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 工作台已读 Service 接口
 *
 * 管理工作台各 Tab 的已读水位线，以及未读数量计算
 */
public interface BpmWorkbenchService {

    /**
     * 获取工作台各 Tab 的未读数量
     *
     * @param userId 用户ID
     * @return tabKey -> 未读数量 的映射
     */
    Map<String, Long> getUnreadCounts(Long userId);

    /**
     * 标记指定 Tab 为已读（更新水位线）
     *
     * @param userId 用户ID
     * @param tabKey Tab 标识
     */
    void markTabAsRead(Long userId, String tabKey);

}
