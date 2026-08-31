package cn.dh.edu.module.bpm.dal.dataobject.task;

import cn.dh.edu.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 工作台 Tab 已读水位线 DO
 *
 * 记录用户每个 Tab 最后一次查看的时间，用于计算"新增未读数"
 */
@TableName(value = "bpm_workbench_read", autoResultMap = true)
@KeySequence("bpm_workbench_read_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmWorkbenchReadDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * Tab 标识（todo / myBill / done / copy）
     */
    private String tabKey;

    /**
     * 最后查看时间
     */
    private LocalDateTime lastViewTime;

}
