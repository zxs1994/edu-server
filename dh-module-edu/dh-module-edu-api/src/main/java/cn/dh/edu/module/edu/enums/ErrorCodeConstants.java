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
    ErrorCode ACTIVITY_BUDGET_INVALID = new ErrorCode(1_051_003_002, "预算总额必须大于 0");
    ErrorCode ACTIVITY_FEE_STANDARD_REQUIRED = new ErrorCode(1_051_003_003, "请至少配置一条费用标准");
    ErrorCode ACTIVITY_FEE_EXCEED_BUDGET = new ErrorCode(1_051_003_004, "费用标准合计不能超过预算总额");
    ErrorCode ACTIVITY_OWNER_REQUIRED = new ErrorCode(1_051_003_005, "请至少选择一名活动负责人");
    ErrorCode ACTIVITY_NAME_REQUIRED = new ErrorCode(1_051_003_006, "活动名称不能为空");
    ErrorCode ACTIVITY_TYPE_REQUIRED = new ErrorCode(1_051_003_007, "请选择活动类型");
    ErrorCode ACTIVITY_START_DATE_REQUIRED = new ErrorCode(1_051_003_008, "请选择开始日期");
    ErrorCode ACTIVITY_FEE_INCOMPLETE = new ErrorCode(1_051_003_010, "费用标准存在未填完整的行");
    ErrorCode ACTIVITY_OWNER_STUDENT_FORBIDDEN = new ErrorCode(1_051_003_011, "负责人不能选择学生角色用户");
    ErrorCode ACTIVITY_PARTICIPANT_ROLE_INVALID = new ErrorCode(1_051_003_012, "参与人须为学生或教培角色用户");

}
