package cn.dh.oa.module.oa.service.contract;

import java.util.*;
import jakarta.validation.*;
import cn.dh.oa.module.oa.controller.admin.contract.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.contract.ContractBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;

/**
 * 合同审批单 Service 接口
 *
 * @author 鼎衡
 */
public interface ContractBillService {

    /**
     * 保存合同审批单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long saveContractBill(@Valid ContractBillSaveReqVO saveReqVO);

    /**
     * 提交合同审批单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long submitContractBill(@Valid ContractBillSaveReqVO saveReqVO);

    /**
     * 创建合同审批单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createContractBill(@Valid ContractBillSaveReqVO createReqVO);

    /**
     * 更新合同审批单
     *
     * @param updateReqVO 更新信息
     */
    void updateContractBill(@Valid ContractBillSaveReqVO updateReqVO);

    /**
     * 删除合同审批单
     *
     * @param id 编号
     */
    void deleteContractBill(Long id);

    /**
     * 批量删除合同审批单
     *
     * @param ids 编号列表
     */
    void deleteContractBillListByIds(List<Long> ids);

    /**
     * 获得合同审批单
     *
     * @param id 编号
     * @return 合同审批单
     */
    ContractBillDO getContractBill(Long id);

    /**
     * 获得合同审批单（包含附件信息）
     *
     * @param id 编号
     * @return 合同审批单响应VO
     */
    ContractBillRespVO getContractBillInfo(Long id);

    /**
     * 获得合同审批单分页
     *
     * @param pageReqVO 分页查询
     * @return 合同审批单分页
     */
    PageResult<ContractBillDO> getContractBillPage(ContractBillPageReqVO pageReqVO);

}
