package cn.dh.edu.module.edu.enums;

import cn.dh.edu.framework.common.exception.ErrorCode;

/**
 * EDU 错误码枚举类
 * <p>
 * EDU 系统，使用 1-051-000-000 段
 * <p>
 * 已占用段参考：infra 1-001、system 1-002、bpm 1-009、hrm 1-050
 */
public interface ErrorCodeConstants {

    // ========== 学生档案 1-051-001-000 ==========
    ErrorCode STUDENT_NOT_EXISTS = new ErrorCode(1_051_001_001, "学生档案不存在");
    ErrorCode STUDENT_NO_EXISTS = new ErrorCode(1_051_001_002, "学号已存在");
    ErrorCode STUDENT_ROLE_NOT_EXISTS = new ErrorCode(1_051_001_003, "学生角色不存在，请先在角色管理中创建编码为 student 的角色");
    ErrorCode STUDENT_USERNAME_INVALID = new ErrorCode(1_051_001_004, "用户账号不符合规则，需为 4-30 位字母或数字");
    ErrorCode STUDENT_USERNAME_EXISTS = new ErrorCode(1_051_001_005, "用户账号已存在");
    ErrorCode STUDENT_MOBILE_REQUIRED = new ErrorCode(1_051_001_006, "手机号不能为空");

    // ========== 教培档案 1-051-002-000 ==========
    ErrorCode TEACHER_NOT_EXISTS = new ErrorCode(1_051_002_001, "教培档案不存在");
    ErrorCode TEACHER_ROLE_NOT_EXISTS = new ErrorCode(1_051_002_002, "教培角色不存在，请先在角色管理中创建编码为 teacher 的角色");
    ErrorCode TEACHER_USERNAME_INVALID = new ErrorCode(1_051_002_003, "用户账号不符合规则，需为 4-30 位字母或数字");
    ErrorCode TEACHER_USERNAME_EXISTS = new ErrorCode(1_051_002_004, "用户账号已存在");
    ErrorCode TEACHER_MOBILE_REQUIRED = new ErrorCode(1_051_002_005, "手机号不能为空");

    // ========== 专项活动 1-051-003-000 ==========
    ErrorCode ACTIVITY_NOT_EXISTS = new ErrorCode(1_051_003_001, "专项活动不存在");
    ErrorCode ACTIVITY_BUDGET_INVALID = new ErrorCode(1_051_003_002, "费用标准合计（折算人民币）必须大于 0");
    ErrorCode ACTIVITY_FEE_STANDARD_REQUIRED = new ErrorCode(1_051_003_003, "请至少配置一条费用标准");
    ErrorCode ACTIVITY_FEE_EXCEED_BUDGET = new ErrorCode(1_051_003_004, "费用标准合计（折算人民币）不能超过预算总额");
    ErrorCode ACTIVITY_OWNER_REQUIRED = new ErrorCode(1_051_003_005, "请至少选择一名活动负责人");
    ErrorCode ACTIVITY_NAME_REQUIRED = new ErrorCode(1_051_003_006, "活动名称不能为空");
    ErrorCode ACTIVITY_TYPE_REQUIRED = new ErrorCode(1_051_003_007, "请选择活动类型");
    ErrorCode ACTIVITY_START_DATE_REQUIRED = new ErrorCode(1_051_003_008, "请选择活动开始时间");
    ErrorCode ACTIVITY_FEE_INCOMPLETE = new ErrorCode(1_051_003_010, "费用标准存在未填完整的行");
    ErrorCode ACTIVITY_FEE_CURRENCY_UNSUPPORTED = new ErrorCode(1_051_003_015, "不支持的费用币种：{}");
    ErrorCode ACTIVITY_EXCHANGE_RATE_FETCH_FAILED = new ErrorCode(1_051_003_016, "获取{}实时汇率失败，请稍后重试");
    ErrorCode ACTIVITY_OWNER_STUDENT_FORBIDDEN = new ErrorCode(1_051_003_011, "负责人不能选择学生角色用户");
    ErrorCode ACTIVITY_PARTICIPANT_ROLE_INVALID = new ErrorCode(1_051_003_012, "参与人须为学生或教培角色用户");
    ErrorCode ACTIVITY_CYCLE_TYPE_NOT_SUPPORTED = new ErrorCode(1_051_003_013, "当前周期类型暂不支持自动生成实例");
    ErrorCode ACTIVITY_INSTANCE_NOT_EXISTS = new ErrorCode(1_051_003_014, "活动实例不存在");
    ErrorCode ACTIVITY_INSTANCE_RECORD_NOT_EDITABLE = new ErrorCode(1_051_003_017, "当前实例状态不允许保存活动记录");
    ErrorCode ACTIVITY_INSTANCE_RECORD_ATTACHMENT_LIMIT = new ErrorCode(1_051_003_018, "活动记录附件最多5个");
    ErrorCode ACTIVITY_INSTANCE_RECORD_TIME_INVALID = new ErrorCode(1_051_003_019, "实际结束时间不能早于开始时间");
    ErrorCode ACTIVITY_INSTANCE_RECORD_ABSENT_INVALID = new ErrorCode(1_051_003_020, "缺席人员必须在活动参与人范围内");
    ErrorCode ACTIVITY_INSTANCE_RECORD_STUDENT_FORBIDDEN = new ErrorCode(1_051_003_044, "学生不可保存活动记录");
    ErrorCode ACTIVITY_INSTANCE_RECORD_OWNER_OR_ADMIN_ONLY = new ErrorCode(1_051_003_045, "仅活动负责人或管理员可保存活动记录");
    ErrorCode ACTIVITY_PARTICIPANT_REQUIRED = new ErrorCode(1_051_003_021, "请至少选择一名活动参与人");
    ErrorCode ACTIVITY_PARTICIPANT_TEACHER_REQUIRED = new ErrorCode(1_051_003_022, "请至少选择一名教培参与人");
    ErrorCode ACTIVITY_PARTICIPANT_STUDENT_REQUIRED = new ErrorCode(1_051_003_023, "请至少选择一名学生参与人");
    ErrorCode ACTIVITY_CONTENT_REQUIRED = new ErrorCode(1_051_003_024, "活动内容不能为空");
    ErrorCode ACTIVITY_ENROLL_TIME_REQUIRED = new ErrorCode(1_051_003_033, "请选择报名时间");
    ErrorCode ACTIVITY_ENROLL_TIME_INVALID = new ErrorCode(1_051_003_034, "报名结束时间不能早于开始时间");
    ErrorCode ACTIVITY_ENROLL_END_AFTER_START_DATE = new ErrorCode(1_051_003_035, "报名结束时间须早于活动开始时间");
    ErrorCode ACTIVITY_INSTANCE_ENROLL_NOT_OPEN = new ErrorCode(1_051_003_025, "当前不在报名时间范围内");
    ErrorCode ACTIVITY_INSTANCE_ENROLL_STATUS_INVALID = new ErrorCode(1_051_003_026, "当前实例状态不允许报名");
    ErrorCode ACTIVITY_INSTANCE_ENROLL_NOT_PARTICIPANT = new ErrorCode(1_051_003_027, "您不在活动参与人范围内，无法报名");
    ErrorCode ACTIVITY_INSTANCE_ENROLL_ALREADY = new ErrorCode(1_051_003_028, "您已报名该活动实例");
    ErrorCode ACTIVITY_INSTANCE_ENROLL_NOT_FOUND = new ErrorCode(1_051_003_029, "未找到报名记录");
    ErrorCode ACTIVITY_INSTANCE_ENROLL_LIMIT = new ErrorCode(1_051_003_030, "报名人数已满");
    ErrorCode ACTIVITY_INSTANCE_ENROLL_CANCEL_INVALID = new ErrorCode(1_051_003_031, "当前不允许取消报名");
    ErrorCode ACTIVITY_INSTANCE_ENROLL_STUDENT_ONLY = new ErrorCode(1_051_003_032, "仅学生活动参与人可自助报名");
    ErrorCode ACTIVITY_INSTANCE_ENROLL_TOKEN_INVALID = new ErrorCode(1_051_003_046, "报名链接无效或已损坏");
    ErrorCode ACTIVITY_INSTANCE_ENROLL_TOKEN_EXPIRED = new ErrorCode(1_051_003_047, "报名链接已过期，请联系老师重新获取");
    ErrorCode ACTIVITY_INSTANCE_FEEDBACK_NOT_EDITABLE = new ErrorCode(1_051_003_036, "当前实例状态不允许提交反馈");
    ErrorCode ACTIVITY_INSTANCE_FEEDBACK_RECORD_REQUIRED = new ErrorCode(1_051_003_037, "请先保存活动记录后再提交反馈");
    ErrorCode ACTIVITY_INSTANCE_STUDENT_FEEDBACK_NOT_REQUIRED = new ErrorCode(1_051_003_038, "您无需提交学生反馈");
    ErrorCode ACTIVITY_INSTANCE_TEACHER_FEEDBACK_NOT_REQUIRED = new ErrorCode(1_051_003_039, "您无需提交教培反馈");
    ErrorCode ACTIVITY_INSTANCE_FEEDBACK_STUDENT_ONLY = new ErrorCode(1_051_003_040, "仅学生角色可提交学生反馈");
    ErrorCode ACTIVITY_INSTANCE_FEEDBACK_TEACHER_ONLY = new ErrorCode(1_051_003_041, "仅教培角色可提交教培反馈");
    ErrorCode ACTIVITY_INSTANCE_ADMIN_FEEDBACK_OWNER_ONLY = new ErrorCode(1_051_003_042, "仅活动负责人可提交班务评价");
    ErrorCode ACTIVITY_INSTANCE_FEEDBACK_ALREADY_SUBMITTED = new ErrorCode(1_051_003_043, "反馈已提交，不可重复填写");

    // ========== 年度预算执行 1-051-004-000 ==========
    ErrorCode REWARD_BUDGET_YEAR_EXISTS = new ErrorCode(1_051_004_009, "该年度预算已存在");
    ErrorCode REWARD_BUDGET_YEAR_NOT_EXISTS = new ErrorCode(1_051_004_010, "年度预算不存在");
    ErrorCode REWARD_BUDGET_PERIOD_MODE_INVALID = new ErrorCode(1_051_004_011, "时段模式无效");
    ErrorCode REWARD_BUDGET_PERIOD_NOT_EXISTS = new ErrorCode(1_051_004_012, "时段预算不存在");
    ErrorCode REWARD_BUDGET_PERIOD_INVALID = new ErrorCode(1_051_004_013, "时段配置无效：请检查名称、日期与金额");
    ErrorCode REWARD_BUDGET_PERIOD_OVERLAP = new ErrorCode(1_051_004_014, "时段日期存在重叠");
    ErrorCode REWARD_BUDGET_PERIOD_NOT_EDITABLE = new ErrorCode(1_051_004_015, "当前时段模式不允许修改日期或增删时段");
    ErrorCode REWARD_BUDGET_YEAR_INVALID = new ErrorCode(1_051_004_016, "预算年度无效");

    // ========== 专项活动付款申请 1-051-005-000 ==========
    ErrorCode ACTIVITY_PAYMENT_REQUEST_NOT_EXISTS = new ErrorCode(1_051_005_001, "付款申请不存在");
    ErrorCode ACTIVITY_PAYMENT_REQUEST_FEE_REQUIRED = new ErrorCode(1_051_005_002, "请至少选择一条费用明细");
    ErrorCode ACTIVITY_PAYMENT_REQUEST_FEE_INVALID = new ErrorCode(1_051_005_003, "存在不可申请的费用明细");
    ErrorCode ACTIVITY_PAYMENT_REQUEST_CURRENCY_MISMATCH = new ErrorCode(1_051_005_004, "同一付款申请的费用明细币种必须一致");
    ErrorCode ACTIVITY_PAYMENT_REQUEST_NOT_EDITABLE = new ErrorCode(1_051_005_005, "当前付款申请状态不允许编辑");
    ErrorCode ACTIVITY_PAYMENT_REQUEST_ACTIVITY_REQUIRED = new ErrorCode(1_051_005_006, "请选择专项活动");
    ErrorCode ACTIVITY_PAYMENT_REQUEST_TITLE_REQUIRED = new ErrorCode(1_051_005_007, "请填写申请事由");
    ErrorCode ACTIVITY_PAYMENT_REQUEST_INSTANCE_REQUIRED = new ErrorCode(1_051_005_008, "请选择活动实例");
    ErrorCode ACTIVITY_PAYMENT_REQUEST_INSTANCE_INVALID = new ErrorCode(1_051_005_009, "活动实例不存在或不属于该活动");
    ErrorCode ACTIVITY_PAYMENT_REQUEST_ACTUAL_AMOUNT_REQUIRED = new ErrorCode(1_051_005_010, "请填写费用明细的实际金额");
    ErrorCode ACTIVITY_PAYMENT_REQUEST_ACTUAL_AMOUNT_INVALID = new ErrorCode(1_051_005_011, "实际金额不能为负数");

}
