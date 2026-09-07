package cn.dh.edu.module.edu.enums.activity;

/**
 * 专项活动公告 / 站内信相关常量
 */
public interface EduActivityNoticeConstants {

    /**
     * 公告类型：专项活动（字典 system_notice_type，value=5）
     */
    Integer NOTICE_TYPE = 5;

    /**
     * 站内信模板：活动实例已生成（通知学生参与人报名）
     */
    String NOTIFY_TEMPLATE_INSTANCE_CREATED = "EDU_ACTIVITY_INSTANCE_CREATED";

    /**
     * 短信模板：活动实例已生成，携带 H5 报名链接
     * 建议变量：activityName、periodNo、plannedDate、enrollUrl
     */
    String SMS_TEMPLATE_INSTANCE_ENROLL = "edu_activity_instance_enroll";

    /**
     * 字典类型：专项活动报名配置
     * 数据键值 h5_base_url → 字典标签填 H5 前端基址（如 https://edu.example.com）
     */
    String DICT_TYPE_ENROLL_CONFIG = "edu_activity_enroll_config";

    /** 字典键：H5 前端基址（标签填域名） */
    String DICT_VALUE_H5_BASE_URL = "h5_base_url";

}
