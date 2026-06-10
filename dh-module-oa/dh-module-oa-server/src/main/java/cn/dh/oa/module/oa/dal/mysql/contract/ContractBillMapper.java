package cn.dh.oa.module.oa.dal.mysql.contract;

import java.util.*;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.oa.dal.dataobject.contract.ContractBillDO;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.oa.module.oa.controller.admin.contract.vo.*;

/**
 * 合同审批单 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface ContractBillMapper extends BaseMapperX<ContractBillDO> {

    default PageResult<ContractBillDO> selectPage(ContractBillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ContractBillDO>()
                .likeIfPresent(ContractBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(ContractBillDO::getProcessStatus, reqVO.getProcessStatus())
                .likeIfPresent(ContractBillDO::getContractTitle, reqVO.getContractTitle())
                .eqIfPresent(ContractBillDO::getContractType, reqVO.getContractType())
                .likeIfPresent(ContractBillDO::getContractParty, reqVO.getContractParty())
                .eqIfPresent(ContractBillDO::getIsMajor, reqVO.getIsMajor())
                .eqIfPresent(ContractBillDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(ContractBillDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(ContractBillDO::getDeptId, reqVO.getDeptId())
                .likeIfPresent(ContractBillDO::getDeptName, reqVO.getDeptName())
                .eqIfPresent(ContractBillDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(ContractBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ContractBillDO::getId));
    }

}
