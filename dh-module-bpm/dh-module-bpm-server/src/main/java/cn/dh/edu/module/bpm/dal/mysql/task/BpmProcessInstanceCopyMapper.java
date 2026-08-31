package cn.dh.edu.module.bpm.dal.mysql.task;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;

import cn.dh.edu.module.bpm.controller.admin.task.vo.instance.BpmProcessInstanceCopyPageReqVO;
import cn.dh.edu.module.bpm.dal.dataobject.task.BpmProcessInstanceCopyDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BpmProcessInstanceCopyMapper extends BaseMapperX<BpmProcessInstanceCopyDO> {

    default PageResult<BpmProcessInstanceCopyDO> selectPage(Long loginUserId, BpmProcessInstanceCopyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BpmProcessInstanceCopyDO>()
                .eqIfPresent(BpmProcessInstanceCopyDO::getUserId, loginUserId)
                .likeIfPresent(BpmProcessInstanceCopyDO::getProcessInstanceName, reqVO.getProcessInstanceName())
                .betweenIfPresent(BpmProcessInstanceCopyDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(BpmProcessInstanceCopyDO::getId));
    }

    default void deleteByProcessInstanceId(String processInstanceId) {
        delete(BpmProcessInstanceCopyDO::getProcessInstanceId, processInstanceId);
    }

    /**
     * 查询用户的未读抄送数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    default Long selectUnreadCount(Long userId) {
        return selectCount(new LambdaQueryWrapperX<BpmProcessInstanceCopyDO>()
                .eq(BpmProcessInstanceCopyDO::getUserId, userId)
                .and(w -> w.isNull(BpmProcessInstanceCopyDO::getReadStatus)
                        .or().eq(BpmProcessInstanceCopyDO::getReadStatus, 0)));
    }

    default java.util.List<BpmProcessInstanceCopyDO> selectUnreadList(Long userId) {
        return selectList(new LambdaQueryWrapperX<BpmProcessInstanceCopyDO>()
                .eq(BpmProcessInstanceCopyDO::getUserId, userId)
                .and(w -> w.isNull(BpmProcessInstanceCopyDO::getReadStatus)
                        .or().eq(BpmProcessInstanceCopyDO::getReadStatus, 0)));
    }

    /**
     * 将用户的所有未读抄送标记为已读
     *
     * @param userId 用户ID
     */
    default void markAllAsRead(Long userId) {
        update(new LambdaUpdateWrapper<BpmProcessInstanceCopyDO>()
                .eq(BpmProcessInstanceCopyDO::getUserId, userId)
                .and(w -> w.isNull(BpmProcessInstanceCopyDO::getReadStatus)
                        .or().eq(BpmProcessInstanceCopyDO::getReadStatus, 0))
                .set(BpmProcessInstanceCopyDO::getReadStatus, 1));
    }

}
