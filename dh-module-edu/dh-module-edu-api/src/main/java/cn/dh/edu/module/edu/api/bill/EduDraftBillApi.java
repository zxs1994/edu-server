package cn.dh.edu.module.edu.api.bill;

import cn.dh.edu.module.edu.api.bill.dto.EduDraftBillQueryDTO;
import cn.dh.edu.module.edu.api.bill.dto.EduDraftBillRespDTO;

import java.util.List;

/**
 * EDU 未提交草稿单据 API（单体模式本地 Bean 实现）
 */
public interface EduDraftBillApi {

    /**
     * 查询指定用户未提交、未关联流程实例的草稿单据
     */
    List<EduDraftBillRespDTO> listMyDraftBills(Long userId, EduDraftBillQueryDTO query);

}
