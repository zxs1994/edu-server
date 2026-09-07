package cn.dh.edu.module.edu.dal.mysql.activity;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPaymentRequestPageReqVO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityPaymentRequestDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ActivityPaymentRequestMapper extends BaseMapperX<ActivityPaymentRequestDO> {

    default PageResult<ActivityPaymentRequestDO> selectPage(ActivityPaymentRequestPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ActivityPaymentRequestDO>()
                .likeIfPresent(ActivityPaymentRequestDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(ActivityPaymentRequestDO::getActivityId, reqVO.getActivityId())
                .eqIfPresent(ActivityPaymentRequestDO::getInstanceId, reqVO.getInstanceId())
                .likeIfPresent(ActivityPaymentRequestDO::getTitle, reqVO.getTitle())
                .eqIfPresent(ActivityPaymentRequestDO::getProcessStatus, reqVO.getProcessStatus())
                .eqIfPresent(ActivityPaymentRequestDO::getApplicantUserId, reqVO.getApplicantUserId())
                .betweenIfPresent(ActivityPaymentRequestDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ActivityPaymentRequestDO::getId));
    }

    /**
     * 查询某前缀下最大流水号（按数字取 MAX；含软删）
     */
    @Select("SELECT MAX(CAST(SUBSTRING(bill_code, CHAR_LENGTH(#{prefix}) + 1) AS UNSIGNED)) "
            + "FROM edu_activity_payment_request WHERE bill_code LIKE CONCAT(#{prefix}, '%')")
    Long selectMaxSequenceByPrefix(@Param("prefix") String prefix);

}
