package cn.dh.oa.module.oa.dal.dataobject.car;

import lombok.*;
import java.util.*;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.*;
import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用车申请单 DO
 *
 * @author 鼎衡
 */
@TableName("oa_car_apply_bill")
@KeySequence("oa_car_apply_bill_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarApplyBillDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 单据编号
     */
    private String billCode;
    /**
     * 流程实例编号
     */
    private String processInstanceId;
    /**
     * 单据状态
     */
    private Integer processStatus;
    /**
     * 车辆ID(逗号分隔)
     */
    private String carId;
    /**
     * 车牌号
     */
    private String carNo;
    /**
     * 出车时间
     */
    private LocalDate goTime;
    /**
     * 回车时间
     */
    private LocalDate returnTime;
    /**
     * 出车地点
     */
    private String goArea;
    /**
     * 回车地点
     */
    private String returnArea;
    /**
     * 用车事由
     */
    private String cause;
    /**
     * 申请人
     */
    private String applyer;
    /**
     * 随行人
     */
    private String passenger;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建者姓名
     */
    private String creatorName;
    /**
     * 父级ID
     */
    private Long parentId;
    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 还车状态
     */
    private Integer returnStatus;


}