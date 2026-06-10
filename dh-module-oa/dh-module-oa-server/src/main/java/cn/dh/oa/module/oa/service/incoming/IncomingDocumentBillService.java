package cn.dh.oa.module.oa.service.incoming;

import jakarta.validation.*;
import cn.dh.oa.module.oa.controller.admin.incoming.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.incoming.IncomingDocumentBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import java.util.*;

public interface IncomingDocumentBillService {

    /**
     * 保存收文办理单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long saveIncomingDocumentBill(@Valid IncomingDocumentBillSaveReqVO saveReqVO);

    /**
     * 提交收文办理单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long submitIncomingDocumentBill(@Valid IncomingDocumentBillSaveReqVO saveReqVO);

    /**
     * 创建收文办理单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createIncomingDocumentBill(@Valid IncomingDocumentBillSaveReqVO createReqVO);

    /**
     * 更新收文办理单
     *
     * @param updateReqVO 更新信息
     */
    void updateIncomingDocumentBill(@Valid IncomingDocumentBillSaveReqVO updateReqVO);

    /**
     * 删除收文办理单
     *
     * @param id 编号
     */
    void deleteIncomingDocumentBill(Long id);

    /**
     * 批量删除收文办理单
     *
     * @param ids 编号列表
     */
    void deleteIncomingDocumentBillListByIds(List<Long> ids);

    /**
     * 获得收文办理单
     *
     * @param id 编号
     * @return 收文办理单
     */
    IncomingDocumentBillDO getIncomingDocumentBill(Long id);

    /**
     * 获得收文办理单详情
     *
     * @param id 编号
     * @return 收文办理单详情
     */
    IncomingDocumentBillRespVO getIncomingDocumentBillInfo(Long id);

    /**
     * 获得收文办理单分页
     *
     * @param pageReqVO 分页查询
     * @return 收文办理单分页
     */
    PageResult<IncomingDocumentBillDO> getIncomingDocumentBillPage(IncomingDocumentBillPageReqVO pageReqVO);

    /**
     * 更新办理状态
     *
     * @param id 编号
     * @param handlingStatus 办理状态
     */
    void updateHandlingStatus(Long id, Integer handlingStatus);

    /**
     * 更新办理结果
     *
     * @param id 编号
     * @param handlingResult 办理结果
     */
    void updateHandlingResult(Long id, String handlingResult);

}
