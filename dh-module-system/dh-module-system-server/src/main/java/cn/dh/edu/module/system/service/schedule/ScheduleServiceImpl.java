package cn.dh.edu.module.system.service.schedule;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.edu.framework.tenant.core.context.TenantContextHolder;
import cn.dh.edu.module.system.controller.admin.schedule.vo.*;
import cn.dh.edu.module.system.dal.dataobject.schedule.ScheduleDO;
import cn.dh.edu.module.system.dal.dataobject.schedule.ScheduleReceiverDO;
import cn.dh.edu.module.system.dal.mysql.schedule.ScheduleMapper;
import cn.dh.edu.module.system.dal.mysql.schedule.ScheduleReceiverMapper;
import cn.dh.edu.module.system.service.user.AdminUserService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.dh.edu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.module.system.enums.ErrorCodeConstants.*;

/**
 * 日程管理 Service 实现类
 *
 * @author 鼎衡
 */
@Service
@Validated
public class ScheduleServiceImpl implements ScheduleService {

    @Resource
    private ScheduleMapper scheduleMapper;

    @Resource
    private ScheduleReceiverMapper scheduleReceiverMapper;

    @Resource
    private AdminUserService adminUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSchedule(ScheduleCreateReqVO createReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        String userName = adminUserService.getUser(userId).getNickname();

        // 插入日程
        ScheduleDO schedule = BeanUtils.toBean(createReqVO, ScheduleDO.class);
        schedule.setCreatorId(userId);
        schedule.setCreatorName(userName);
        schedule.setIsPushed(false); // 创建时默认为非推送，不自动推送
        schedule.setTenantId(TenantContextHolder.getTenantId());
        scheduleMapper.insert(schedule);

        saveReceivers(schedule.getId(), createReqVO.getReceiverIds());

        return schedule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSchedule(ScheduleUpdateReqVO updateReqVO) {
        // 校验日程是否存在
        ScheduleDO schedule = validateScheduleExists(updateReqVO.getId());

        // 校验权限：只有创建人且非推送的才能编辑
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (!schedule.getCreatorId().equals(userId)) {
            throw exception(SCHEDULE_NOT_EDITABLE);
        }

        // 更新日程
        ScheduleDO updateObj = BeanUtils.toBean(updateReqVO, ScheduleDO.class);
        scheduleMapper.updateById(updateObj);

        if (updateReqVO.getReceiverIds() != null) {
            appendReceivers(updateReqVO.getId(), updateReqVO.getReceiverIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSchedule(Long id) {
        // 校验日程是否存在
        ScheduleDO schedule = validateScheduleExists(id);

        // 校验权限：只有创建人且非推送的才能删除
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (!schedule.getCreatorId().equals(userId) || Boolean.TRUE.equals(schedule.getIsPushed())) {
            throw exception(SCHEDULE_NOT_DELETABLE);
        }

        // 删除接收人关系
        scheduleReceiverMapper.physicalDeleteByScheduleId(id);

        // 删除日程
        scheduleMapper.deleteById(id);
    }

    @Override
    public ScheduleDO getSchedule(Long id) {
        return scheduleMapper.selectById(id);
    }

    @Override
    public PageResult<ScheduleDO> getSchedulePage(SchedulePageReqVO pageReqVO) {
        return scheduleMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ScheduleDO> getScheduleListByDate(LocalDate scheduleDate) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return scheduleMapper.selectListByDate(scheduleDate, userId);
    }

    @Override
    public List<String> getScheduleDates(LocalDate startDate, LocalDate endDate) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        Long tenantId = TenantContextHolder.getTenantId();
        List<LocalDate> dates = scheduleMapper.selectScheduleDates(startDate, endDate, userId, tenantId);
        // 转换为 yyyy-MM-dd 格式的字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_YEAR_MONTH_DAY);
        return dates.stream()
                .map(date -> date.format(formatter))
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<ScheduleDO> getMySchedulePage(SchedulePageReqVO pageReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return scheduleMapper.selectMySchedulePage(userId, pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushSchedule(SchedulePushReqVO pushReqVO) {
        // 校验日程是否存在
        ScheduleDO schedule = validateScheduleExists(pushReqVO.getScheduleId());

        // 校验权限：只有创建人才能推送
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (!schedule.getCreatorId().equals(userId)) {
            throw exception(SCHEDULE_NOT_EDITABLE);
        }

        // 仅向未接收的用户推送
        List<Long> receiverIds = resolveReceiverIds(pushReqVO.getScheduleId(), pushReqVO.getReceiverIds());
        List<Long> newReceiverIds = filterNewReceiverIds(pushReqVO.getScheduleId(), receiverIds);
        appendReceivers(pushReqVO.getScheduleId(), newReceiverIds);

        // 更新日程为已推送
        schedule.setIsPushed(true);
        scheduleMapper.updateById(schedule);
    }

    /**
     * 保存待推送接收人（仅追加未接收的用户）
     */
    private void saveReceivers(Long scheduleId, List<Long> receiverIds) {
        if (receiverIds == null || receiverIds.isEmpty()) {
            return;
        }
        appendReceivers(scheduleId, filterNewReceiverIds(scheduleId, receiverIds));
    }

    /**
     * 过滤掉已接收的用户
     */
    private List<Long> filterNewReceiverIds(Long scheduleId, List<Long> receiverIds) {
        Set<Long> existingReceiverIds = scheduleReceiverMapper.selectListByScheduleId(scheduleId).stream()
                .map(ScheduleReceiverDO::getReceiverId)
                .collect(Collectors.toSet());
        return receiverIds.stream()
                .distinct()
                .filter(receiverId -> !existingReceiverIds.contains(receiverId))
                .collect(Collectors.toList());
    }

    /**
     * 追加接收人（不删除已接收记录）
     */
    private void appendReceivers(Long scheduleId, List<Long> receiverIds) {
        if (receiverIds == null || receiverIds.isEmpty()) {
            return;
        }

        Long tenantId = TenantContextHolder.getTenantId();
        List<ScheduleReceiverDO> receivers = receiverIds.stream()
                .map(receiverId -> {
                    ScheduleReceiverDO receiver = new ScheduleReceiverDO();
                    receiver.setScheduleId(scheduleId);
                    receiver.setReceiverId(receiverId);
                    receiver.setReceiverName(adminUserService.getUser(receiverId).getNickname());
                    receiver.setReadStatus(0);
                    receiver.setTenantId(tenantId);
                    return receiver;
                })
                .collect(Collectors.toList());

        scheduleReceiverMapper.insertBatch(receivers);
    }

    /**
     * 解析推送接收人：优先使用请求参数，否则使用已保存的接收人
     */
    private List<Long> resolveReceiverIds(Long scheduleId, List<Long> receiverIds) {
        if (receiverIds != null && !receiverIds.isEmpty()) {
            return receiverIds;
        }
        List<Long> savedReceiverIds = scheduleReceiverMapper.selectListByScheduleId(scheduleId).stream()
                .map(ScheduleReceiverDO::getReceiverId)
                .collect(Collectors.toList());
        if (savedReceiverIds.isEmpty()) {
            throw exception(SCHEDULE_RECEIVERS_EMPTY);
        }
        return savedReceiverIds;
    }

    @VisibleForTesting
    public ScheduleDO validateScheduleExists(Long id) {
        if (id == null) {
            throw exception(SCHEDULE_NOT_FOUND);
        }
        ScheduleDO schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw exception(SCHEDULE_NOT_FOUND);
        }
        return schedule;
    }

}

