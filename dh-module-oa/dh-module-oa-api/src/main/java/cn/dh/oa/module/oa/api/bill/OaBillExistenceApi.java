package cn.dh.oa.module.oa.api.bill;

/**
 * OA 单据存在性检查 API（单体模式本地 Bean 实现）
 */
public interface OaBillExistenceApi {

    /**
     * 判断业务单据是否仍存在（未被逻辑删除）
     */
    boolean exists(String processDefinitionKey, String businessKey);

}
