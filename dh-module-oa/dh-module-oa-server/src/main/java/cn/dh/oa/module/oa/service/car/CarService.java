package cn.dh.oa.module.oa.service.car;

import java.util.*;
import jakarta.validation.*;
import cn.dh.oa.module.oa.controller.admin.car.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.car.CarDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.pojo.PageParam;

/**
 * 车辆信息 Service 接口
 *
 * @author 鼎衡
 */
public interface CarService {

    /**
     * 创建车辆信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCar(@Valid CarSaveReqVO createReqVO);

    /**
     * 更新车辆信息
     *
     * @param updateReqVO 更新信息
     */
    void updateCar(@Valid CarSaveReqVO updateReqVO);

    /**
     * 删除车辆信息
     *
     * @param id 编号
     */
    void deleteCar(Long id);

    /**
    * 批量删除车辆信息
    *
    * @param ids 编号
    */
    void deleteCarListByIds(List<Long> ids);

    /**
     * 获得车辆信息
     *
     * @param id 编号
     * @return 车辆信息
     */
    CarDO getCar(Long id);

    /**
     * 获得车辆信息分页
     *
     * @param pageReqVO 分页查询
     * @return 车辆信息分页
     */
    PageResult<CarDO> getCarPage(CarPageReqVO pageReqVO);

}