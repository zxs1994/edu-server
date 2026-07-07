package cn.dh.oa.module.oa.dal.mysql.contract;

import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.oa.dal.dataobject.contract.ContractCodeSeqDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 合同编号序列 Mapper
 */
@Mapper
public interface ContractCodeSeqMapper extends BaseMapperX<ContractCodeSeqDO> {

    @Select("SELECT id, biz_year, type_code, current_seq, creator, create_time, updater, update_time, deleted, tenant_id " +
            "FROM oa_contract_code_seq " +
            "WHERE biz_year = #{bizYear} AND type_code = #{typeCode} AND deleted = b'0' " +
            "LIMIT 1 FOR UPDATE")
    ContractCodeSeqDO selectByYearAndTypeForUpdate(@Param("bizYear") Integer bizYear, @Param("typeCode") String typeCode);
}
