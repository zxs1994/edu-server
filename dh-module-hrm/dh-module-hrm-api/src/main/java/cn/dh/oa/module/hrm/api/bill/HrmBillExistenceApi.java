package cn.dh.oa.module.hrm.api.bill;

/**
 * HRM 单据存在性检查 API
 */
public interface HrmBillExistenceApi {

    /**
     * 判断业务单据是否仍存在（未被删除）
     */
    boolean exists(String processDefinitionKey, String businessKey);

}
