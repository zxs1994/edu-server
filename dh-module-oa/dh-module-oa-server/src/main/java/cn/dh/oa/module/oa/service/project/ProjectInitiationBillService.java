package cn.dh.oa.module.oa.service.project;

import jakarta.validation.*;
import cn.dh.oa.module.oa.controller.admin.project.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.project.ProjectInitiationBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import java.util.*;

public interface ProjectInitiationBillService {

    /**
     * 保存项目立项单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long saveProjectInitiationBill(@Valid ProjectInitiationBillSaveReqVO saveReqVO);

    /**
     * 提交项目立项单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long submitProjectInitiationBill(@Valid ProjectInitiationBillSaveReqVO saveReqVO);

    /**
     * 创建项目立项单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProjectInitiationBill(@Valid ProjectInitiationBillSaveReqVO createReqVO);

    /**
     * 更新项目立项单
     *
     * @param updateReqVO 更新信息
     */
    void updateProjectInitiationBill(@Valid ProjectInitiationBillSaveReqVO updateReqVO);

    /**
     * 删除项目立项单
     *
     * @param id 编号
     */
    void deleteProjectInitiationBill(Long id);

    /**
     * 批量删除项目立项单
     *
     * @param ids 编号列表
     */
    void deleteProjectInitiationBillListByIds(List<Long> ids);

    /**
     * 获得项目立项单
     *
     * @param id 编号
     * @return 项目立项单
     */
    ProjectInitiationBillDO getProjectInitiationBill(Long id);

    /**
     * 获得项目立项单详情
     *
     * @param id 编号
     * @return 项目立项单详情
     */
    ProjectInitiationBillRespVO getProjectInitiationBillInfo(Long id);

    /**
     * 获得项目立项单分页
     *
     * @param pageReqVO 分页查询
     * @return 项目立项单分页
     */
    PageResult<ProjectInitiationBillDO> getProjectInitiationBillPage(ProjectInitiationBillPageReqVO pageReqVO);

}
