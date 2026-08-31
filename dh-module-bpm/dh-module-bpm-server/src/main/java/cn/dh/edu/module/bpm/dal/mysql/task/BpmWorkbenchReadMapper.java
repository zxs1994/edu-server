package cn.dh.edu.module.bpm.dal.mysql.task;

import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.module.bpm.dal.dataobject.task.BpmWorkbenchReadDO;
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
