package cn.dh.oa.module.oa.service.document;

import java.util.*;
import jakarta.validation.*;
import cn.dh.oa.module.oa.controller.admin.document.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.document.DocumentDispatchBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;

/**
 * 公文发文单 Service 接口
 *
 * @author 鼎衡
 */
public interface DocumentDispatchBillService {

    /**
     * 保存公文发文单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long saveDocumentDispatchBill(@Valid DocumentDispatchBillSaveReqVO saveReqVO);

    /**
     * 提交公文发文单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long submitDocumentDispatchBill(@Valid DocumentDispatchBillSaveReqVO saveReqVO);

    /**
     * 创建公文发文单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDocumentDispatchBill(@Valid DocumentDispatchBillSaveReqVO createReqVO);

    /**
     * 更新公文发文单
     *
     * @param updateReqVO 更新信息
     */
    void updateDocumentDispatchBill(@Valid DocumentDispatchBillSaveReqVO updateReqVO);

    /**
     * 删除公文发文单
     *
     * @param id 编号
     */
    void deleteDocumentDispatchBill(Long id);

    /**
     * 批量删除公文发文单
     *
     * @param ids 编号列表
     */
    void deleteDocumentDispatchBillListByIds(List<Long> ids);

    /**
     * 获得公文发文单
     *
     * @param id 编号
     * @return 公文发文单
     */
    DocumentDispatchBillDO getDocumentDispatchBill(Long id);

    /**
     * 获得公文发文单（包含附件信息）
     *
     * @param id 编号
     * @return 公文发文单响应VO
     */
    DocumentDispatchBillRespVO getDocumentDispatchBillInfo(Long id);

    /**
     * 获得公文发文单分页
     *
     * @param pageReqVO 分页查询
     * @return 公文发文单分页
     */
    PageResult<DocumentDispatchBillDO> getDocumentDispatchBillPage(DocumentDispatchBillPageReqVO pageReqVO);

}
