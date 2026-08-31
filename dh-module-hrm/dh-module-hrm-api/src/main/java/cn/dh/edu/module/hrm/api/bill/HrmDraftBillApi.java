package cn.dh.edu.module.hrm.api.bill;

import cn.dh.edu.module.hrm.api.bill.dto.HrmDraftBillQueryDTO;
import cn.dh.edu.module.hrm.api.bill.dto.HrmDraftBillRespDTO;

import java.util.List;

/**
 * HRM 未提交草稿单据 API（单体模式本地 Bean 实现）
 */
public interface HrmDraftBillApi {

    /**
     * 查询指定用户未提交、未关联流程实例的草稿单据
     */
    List<HrmDraftBillRespDTO> listMyDraftBills(Long userId, HrmDraftBillQueryDTO query);

}
