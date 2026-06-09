package cn.dh.oa.module.oa.dal.mysql.meetingroom;

import java.util.*;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.oa.dal.dataobject.meetingroom.MeetingRoomDO;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.oa.module.oa.controller.admin.meetingroom.vo.*;

/**
 * 会议室信息 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface MeetingRoomMapper extends BaseMapperX<MeetingRoomDO> {

    default PageResult<MeetingRoomDO> selectPage(MeetingRoomPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MeetingRoomDO>()
                .likeIfPresent(MeetingRoomDO::getRoomName, reqVO.getRoomName())
                .likeIfPresent(MeetingRoomDO::getRoomLocation, reqVO.getRoomLocation())
                .eqIfPresent(MeetingRoomDO::getRoomType, reqVO.getRoomType())
                .likeIfPresent(MeetingRoomDO::getManagerName, reqVO.getManagerName())
                .eqIfPresent(MeetingRoomDO::getAvailableStatus, reqVO.getAvailableStatus())
                .eqIfPresent(MeetingRoomDO::getAllowBooking, reqVO.getAllowBooking())
                .betweenIfPresent(MeetingRoomDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(MeetingRoomDO::getSort)
                .orderByDesc(MeetingRoomDO::getId));
    }

}

