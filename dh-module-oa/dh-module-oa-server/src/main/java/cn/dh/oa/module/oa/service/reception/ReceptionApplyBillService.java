package cn.dh.oa.module.oa.service.reception;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.module.oa.controller.admin.reception.vo.ReceptionApplyBillPageReqVO;
import cn.dh.oa.module.oa.controller.admin.reception.vo.ReceptionApplyBillRespVO;
import cn.dh.oa.module.oa.controller.admin.reception.vo.ReceptionApplyBillSaveReqVO;
import cn.dh.oa.module.oa.dal.dataobject.reception.ReceptionApplyBillDO;
import jakarta.validation.Valid;

import java.util.List;

public interface ReceptionApplyBillService {

    Long saveReceptionApplyBill(@Valid ReceptionApplyBillSaveReqVO saveReqVO);

    Long submitReceptionApplyBill(@Valid ReceptionApplyBillSaveReqVO saveReqVO);

    void deleteReceptionApplyBill(Long id);

    void deleteReceptionApplyBillListByIds(List<Long> ids);

    ReceptionApplyBillDO getReceptionApplyBill(Long id);

    ReceptionApplyBillRespVO getReceptionApplyBillInfo(Long id);

    PageResult<ReceptionApplyBillDO> getReceptionApplyBillPage(ReceptionApplyBillPageReqVO pageReqVO);

}
