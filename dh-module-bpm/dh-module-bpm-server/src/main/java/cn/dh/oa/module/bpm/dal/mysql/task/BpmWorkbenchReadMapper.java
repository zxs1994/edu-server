package cn.dh.oa.module.bpm.dal.mysql.task;

import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.module.bpm.dal.dataobject.task.BpmWorkbenchReadDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BpmWorkbenchReadMapper extends BaseMapperX<BpmWorkbenchReadDO> {

    /**
     * 查询用户指定 Tab 的水位线记录
     */
    default BpmWorkbenchReadDO selectByUserIdAndTabKey(Long userId, String tabKey) {
        return selectOne(new LambdaQueryWrapperX<BpmWorkbenchReadDO>()
                .eq(BpmWorkbenchReadDO::getUserId, userId)
                .eq(BpmWorkbenchReadDO::getTabKey, tabKey));
    }

}
