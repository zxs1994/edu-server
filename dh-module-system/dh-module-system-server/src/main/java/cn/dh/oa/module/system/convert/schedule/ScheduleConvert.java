package cn.dh.oa.module.system.convert.schedule;

import cn.dh.oa.framework.common.util.collection.CollectionUtils;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import cn.dh.oa.module.system.controller.admin.schedule.vo.ScheduleCreateReqVO;
import cn.dh.oa.module.system.controller.admin.schedule.vo.ScheduleRespVO;
import cn.dh.oa.module.system.controller.admin.schedule.vo.ScheduleUpdateReqVO;
import cn.dh.oa.module.system.dal.dataobject.schedule.ScheduleDO;
import cn.dh.oa.module.system.dal.dataobject.schedule.ScheduleReceiverDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ScheduleConvert {

    ScheduleConvert INSTANCE = Mappers.getMapper(ScheduleConvert.class);

    ScheduleDO convert(ScheduleCreateReqVO bean);

    ScheduleDO convert(ScheduleUpdateReqVO bean);

    ScheduleRespVO convert(ScheduleDO bean);

    List<ScheduleRespVO> convertList(List<ScheduleDO> list);

    /**
     * 转换日程响应VO（包含接收人列表）
     *
     * @param schedule 日程
     * @param receivers 接收人列表
     * @return 响应VO
     */
    default ScheduleRespVO convert(ScheduleDO schedule, List<ScheduleReceiverDO> receivers) {
        ScheduleRespVO respVO = BeanUtils.toBean(schedule, ScheduleRespVO.class);
        if (receivers != null && !receivers.isEmpty()) {
            respVO.setReceivers(CollectionUtils.convertList(receivers, receiver -> {
                ScheduleRespVO.ReceiverVO receiverVO = new ScheduleRespVO.ReceiverVO();
                receiverVO.setReceiverId(receiver.getReceiverId());
                receiverVO.setReceiverName(receiver.getReceiverName());
                receiverVO.setReadStatus(receiver.getReadStatus());
                receiverVO.setReadTime(receiver.getReadTime());
                return receiverVO;
            }));
        }
        return respVO;
    }

}

