package cn.dh.edu.module.edu.enums;

import cn.dh.edu.framework.common.enums.BillTypeEnum;

/**
 * EDU 单据类型枚举
 *
 * @author 鼎衡
 */
public enum EduBillTypeEnum implements BillTypeEnum {

    /**
     * 专项活动
     */
    ACTIVITY("301", "专项活动", "edu_activity");

    private final String code;
    private final String name;
    private final String processDefinitionKey;

    EduBillTypeEnum(String code, String name, String processDefinitionKey) {
        this.code = code;
        this.name = name;
        this.processDefinitionKey = processDefinitionKey;
    }

    @Override
    public String getTypeCode() {
        return code;
    }

    @Override
    public String getTypeName() {
        return name;
    }

    @Override
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public static EduBillTypeEnum getByProcessDefinitionKey(String processDefinitionKey) {
        if (processDefinitionKey == null) {
            return null;
        }
        for (EduBillTypeEnum value : values()) {
            if (processDefinitionKey.equals(value.processDefinitionKey)) {
                return value;
            }
        }
        return null;
    }

}
