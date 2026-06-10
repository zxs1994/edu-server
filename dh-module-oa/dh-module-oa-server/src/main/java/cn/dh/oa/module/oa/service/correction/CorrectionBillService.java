package cn.dh.oa.module.oa.service.correction;

import java.util.*;
import jakarta.validation.*;
import cn.dh.oa.module.oa.controller.admin.correction.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.correction.CorrectionBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;

/**
 * 纠错申请单 Service 接口
 *
 * @author 鼎衡
 */
public interface CorrectionBillService {

    /**
     * 保存纠错申请单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long saveCorrectionBill(@Valid CorrectionBillSaveReqVO saveReqVO);

    /**
     * 提交纠错申请单（保存并冻结原单据）
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long submitCorrectionBill(@Valid CorrectionBillSaveReqVO saveReqVO);

    /**
     * 更新纠错申请单
     *
     * @param updateReqVO 更新信息
     */
    void updateCorrectionBill(@Valid CorrectionBillSaveReqVO updateReqVO);

    /**
     * 删除纠错申请单
     *
     * @param id 编号
     */
    void deleteCorrectionBill(Long id);

    /**
     * 批量删除纠错申请单
     *
     * @param ids 编号列表
     */
    void deleteCorrectionBillListByIds(List<Long> ids);

    /**
     * 获得纠错申请单
     *
     * @param id 编号
     * @return 纠错申请单
     */
    CorrectionBillDO getCorrectionBill(Long id);

    /**
     * 获得纠错申请单（包含附件信息）
     *
     * @param id 编号
     * @return 纠错申请单响应VO
     */
    CorrectionBillRespVO getCorrectionBillInfo(Long id);

    /**
     * 获得纠错申请单分页
     *
     * @param pageReqVO 分页查询
     * @return 纠错申请单分页
     */
    PageResult<CorrectionBillDO> getCorrectionBillPage(CorrectionBillPageReqVO pageReqVO);

    /**
     * 冻结原单据执行
     *
     * @param businessKey 纠错申请单业务Key（ID）
     */
    void freezeSource(String businessKey);

    /**
     * 解冻原单据
     *
     * @param businessKey 纠错申请单业务Key（ID）
     */
    void unfreezeSource(String businessKey);

    /**
     * 发起重审流程
     *
     * @param id 纠错申请单编号
     */
    void startReApproval(Long id);

}
