package cn.dh.edu.module.edu.enums;

import cn.dh.edu.framework.common.enums.RpcConstants;

/**
 * API 相关的枚举
 * <p>
 * 数据库表前缀约定：{@code edu_}
 *
 * @author 鼎衡
 */
public class ApiConstants {

    /**
     * 服务名
     *
     * 注意，需要保证和 spring.application.name 保持一致
     */
    public static final String NAME = "edu-server";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/edu";

    public static final String VERSION = "1.0.0";

}
