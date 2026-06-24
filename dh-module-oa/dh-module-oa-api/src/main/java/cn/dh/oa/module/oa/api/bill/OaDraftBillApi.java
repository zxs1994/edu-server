package cn.dh.oa.module.oa.api.bill;

import cn.dh.oa.module.oa.api.bill.dto.OaDraftBillQueryDTO;
import cn.dh.oa.module.oa.api.bill.dto.OaDraftBillRespDTO;

import java.util.List;

/**
 * OA 未提交草稿单据 API（单体模式本地 Bean 实现）
 */
public interface OaDraftBillApi {

    /**
     * 查询指定用户未提交、未关联流程实例的草稿单据
     */
    List<OaDraftBillRespDTO> listMyDraftBills(Long userId, OaDraftBillQueryDTO query);

}
