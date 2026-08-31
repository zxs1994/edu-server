package cn.dh.edu.module.edu.dal.mysql.activity;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPageReqVO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 专项活动 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface ActivityMapper extends BaseMapperX<ActivityDO> {

    default PageResult<ActivityDO> selectPage(ActivityPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ActivityDO>()
                .likeIfPresent(ActivityDO::getBillCode, reqVO.getBillCode())
                .likeIfPresent(ActivityDO::getName, reqVO.getName())
                .eqIfPresent(ActivityDO::getActivityType, reqVO.getActivityType())
                .eqIfPresent(ActivityDO::getProcessStatus, reqVO.getProcessStatus())
                .eqIfPresent(ActivityDO::getCycleType, reqVO.getCycleType())
                .betweenIfPresent(ActivityDO::getStartDate, reqVO.getStartDate())
                .eqIfPresent(ActivityDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(ActivityDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ActivityDO::getId));
    }

    /**
     * 查询某前缀下最大流水号（按数字取 MAX，避免超过 3 位后字符串排序错误；含软删）
     */
    @Select("SELECT MAX(CAST(SUBSTRING(bill_code, CHAR_LENGTH(#{prefix}) + 1) AS UNSIGNED)) "
            + "FROM edu_activity WHERE bill_code LIKE CONCAT(#{prefix}, '%')")
    Long selectMaxSequenceByPrefix(@Param("prefix") String prefix);

}
