package cn.dh.edu.module.edu.controller.admin.activity;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPageReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivitySaveReqVO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityOwnerDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityParticipantDO;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityOwnerMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityParticipantMapper;
import cn.dh.edu.module.edu.service.activity.ActivityService;
import cn.dh.edu.module.system.api.user.AdminUserApi;
import cn.dh.edu.module.system.api.user.dto.AdminUserRespDTO;
import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.dh.edu.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 专项活动")
@RestController
@RequestMapping("/edu/activity")
@Validated
public class ActivityController {

    @Resource
    private ActivityService activityService;
    @Resource
    private ActivityOwnerMapper activityOwnerMapper;
    @Resource
    private ActivityParticipantMapper activityParticipantMapper;
    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/save")
    @Operation(summary = "保存专项活动（草稿）")
    @PreAuthorize("@ss.hasPermission('edu:activity:create')")
    public CommonResult<Long> saveActivity(@Valid @RequestBody ActivitySaveReqVO saveReqVO) {
        return success(activityService.saveActivity(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交专项活动审核")
    @PreAuthorize("@ss.hasPermission('edu:activity:submit')")
    public CommonResult<Long> submitActivity(@Valid @RequestBody ActivitySaveReqVO submitReqVO) {
        return success(activityService.submitActivity(submitReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除专项活动")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('edu:activity:delete')")
    public CommonResult<Boolean> deleteActivity(@RequestParam("id") Long id) {
        activityService.deleteActivity(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除专项活动")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('edu:activity:delete')")
    public CommonResult<Boolean> deleteActivityList(@RequestParam("ids") List<Long> ids) {
        activityService.deleteActivityListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得专项活动")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('edu:activity:query')")
    public CommonResult<ActivityRespVO> getActivity(@RequestParam("id") Long id) {
        return success(activityService.getActivity(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得专项活动分页")
    @PreAuthorize("@ss.hasPermission('edu:activity:query')")
    public CommonResult<PageResult<ActivityRespVO>> getActivityPage(@Valid ActivityPageReqVO pageReqVO) {
        PageResult<ActivityDO> pageResult = activityService.getActivityPage(pageReqVO);
        PageResult<ActivityRespVO> voPage = BeanUtils.toBean(pageResult, ActivityRespVO.class);
        fillOwnerNames(voPage.getList());
        fillParticipantNames(voPage.getList());
        return success(voPage);
    }

    private void fillOwnerNames(List<ActivityRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        for (ActivityRespVO item : list) {
            List<ActivityOwnerDO> owners = activityOwnerMapper.selectListByActivityId(item.getId());
            List<Long> ownerIds = owners.stream().map(ActivityOwnerDO::getUserId).filter(Objects::nonNull).toList();
            item.setOwnerUserIds(ownerIds);
            if (CollUtil.isEmpty(ownerIds)) {
                item.setOwnerUserNames("");
                continue;
            }
            Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(ownerIds);
            List<String> names = new ArrayList<>();
            for (Long id : ownerIds) {
                AdminUserRespDTO user = userMap.get(id);
                if (user != null && StringUtils.isNotBlank(user.getNickname())) {
                    names.add(user.getNickname());
                }
            }
            item.setOwnerUserNames(String.join("、", names));
        }
    }

    private void fillParticipantNames(List<ActivityRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        for (ActivityRespVO item : list) {
            List<ActivityParticipantDO> participants =
                    activityParticipantMapper.selectListByActivityId(item.getId());
            List<Long> userIds = participants.stream()
                    .map(ActivityParticipantDO::getUserId)
                    .filter(Objects::nonNull)
                    .toList();
            item.setParticipantUserIds(userIds);
            if (CollUtil.isEmpty(userIds)) {
                item.setParticipantNames("");
                continue;
            }
            Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(userIds);
            List<String> names = new ArrayList<>();
            for (Long id : userIds) {
                AdminUserRespDTO user = userMap.get(id);
                if (user == null) {
                    continue;
                }
                if (StringUtils.isNotBlank(user.getNickname()) && StringUtils.isNotBlank(user.getUsername())) {
                    names.add(user.getNickname() + "(" + user.getUsername() + ")");
                } else if (StringUtils.isNotBlank(user.getNickname())) {
                    names.add(user.getNickname());
                } else if (StringUtils.isNotBlank(user.getUsername())) {
                    names.add(user.getUsername());
                }
            }
            item.setParticipantNames(String.join("、", names));
        }
    }

}
