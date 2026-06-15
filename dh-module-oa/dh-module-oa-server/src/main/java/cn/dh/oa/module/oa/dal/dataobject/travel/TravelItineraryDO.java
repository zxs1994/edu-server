package cn.dh.oa.module.oa.dal.dataobject.travel;

import lombok.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;

/**
 * 差旅行程明细 DO
 */
@TableName("oa_travel_itinerary")
@KeySequence("oa_travel_itinerary_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelItineraryDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 差旅申请单ID
     */
    private Long billId;
    /**
     * 出发城市
     */
    private String departureCity;
    /**
     * 到达城市
     */
    private String destinationCity;
    /**
     * 开始日期
     */
    private LocalDateTime startDate;
    /**
     * 结束日期
     */
    private LocalDateTime endDate;
    /**
     * 交通方式：1火车 2飞机 3自驾 4公务用车 5其他
     */
    private Integer transportType;
    /**
     * 备注
     */
    private String remark;
    /**
     * 排序
     */
    private Integer sortOrder;

}
