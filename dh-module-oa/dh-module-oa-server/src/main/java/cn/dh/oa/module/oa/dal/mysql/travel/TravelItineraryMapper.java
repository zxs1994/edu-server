package cn.dh.oa.module.oa.dal.mysql.travel;

import java.util.*;

import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.module.oa.dal.dataobject.travel.TravelItineraryDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 差旅行程明细 Mapper
 */
@Mapper
public interface TravelItineraryMapper extends BaseMapperX<TravelItineraryDO> {

    default List<TravelItineraryDO> selectListByBillId(Long billId) {
        return selectList(new LambdaQueryWrapperX<TravelItineraryDO>()
                .eq(TravelItineraryDO::getBillId, billId)
                .orderByAsc(TravelItineraryDO::getSortOrder));
    }

    default void deleteByBillId(Long billId) {
        delete(new LambdaQueryWrapperX<TravelItineraryDO>()
                .eq(TravelItineraryDO::getBillId, billId));
    }

}
