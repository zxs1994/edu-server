package cn.dh.oa.module.oa.enums;

import cn.dh.oa.framework.common.exception.ErrorCode;

/**
 * oa 错误码枚举类
 *
 * oa 系统，使用 1-101-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== oa车辆管理 1-101-000-000 ============
    ErrorCode CAR_NOT_EXISTS = new ErrorCode(1_101_000_000, "车辆信息不存在");

    // ========== 用车申请单 ==========
    ErrorCode CAR_APPLY_BILL_NOT_EXISTS = new ErrorCode(1_101_000_001, "用车申请单不存在");
    ErrorCode CAR_APPLY_SAVE_INFO_NOT_NULL = new ErrorCode(1_101_000_002, "保存信息不能为空");
    // ========== 还车申请单  ==========
    ErrorCode CAR_RETURN_BILL_NOT_EXISTS = new ErrorCode(1_101_000_003, "还车申请单不存在");
    ErrorCode CAR_APPLY_BILL_ALREADY_RETURNED = new ErrorCode(1_101_000_004, "用车申请单已还车，不能重复还车");
    ErrorCode CAR_TIME_CONFLICT = new ErrorCode(1_101_000_005, "车辆使用时间冲突，该时间段已有其他申请单");

    // ========== 印章管理 1-101-001-000 ============
    ErrorCode SEAL_NOT_EXISTS = new ErrorCode(1_101_001_000, "印章信息不存在");
    ErrorCode SEAL_NO_DUPLICATE = new ErrorCode(1_101_001_001, "印章编号已存在");
    
    // ========== 用印申请单 1-101-001-100 ============
    ErrorCode SEAL_APPLY_BILL_NOT_EXISTS = new ErrorCode(1_101_001_100, "用印申请单不存在");
    ErrorCode SEAL_APPLY_SAVE_INFO_NOT_NULL = new ErrorCode(1_101_001_101, "保存信息不能为空");
    ErrorCode SEAL_TIME_CONFLICT = new ErrorCode(1_101_001_102, "印章使用时间冲突，该时间段已有其他申请单");
    ErrorCode SEAL_APPLY_BILL_ALREADY_USED = new ErrorCode(1_101_001_103, "用印申请单已使用，不能重复使用");
    ErrorCode SEAL_APPLY_BILL_NOT_APPROVED = new ErrorCode(1_101_001_104, "用印申请单未审批通过，不能使用");
    ErrorCode SEAL_APPLY_BILL_OVERDUE = new ErrorCode(1_101_001_105, "印章借用已逾期，请及时归还");

    // ========== 通用附件 1-101-002-000 ============
    ErrorCode ATTACHMENT_NOT_EXISTS = new ErrorCode(1_101_002_000, "附件不存在");

    // ========== 企业云盘 1-101-003-000 ============
    ErrorCode FILE_INFO_NOT_EXISTS = new ErrorCode(1_101_003_000, "文件/文件夹不存在");
    ErrorCode FILE_SHARE_NOT_OWNER = new ErrorCode(1_101_003_001, "只有文件所有者才能分享文件");
    ErrorCode FILE_SHARE_NO_PERMISSION = new ErrorCode(1_101_003_002, "无权限访问");
    ErrorCode FILE_CANCEL_SHARE_NOT_OWNER = new ErrorCode(1_101_003_003, "只有文件所有者才能取消分享");

    // ========== 会议室管理 1-101-004-000 ============
    ErrorCode MEETING_ROOM_NOT_EXISTS = new ErrorCode(1_101_004_000, "会议室信息不存在");

    // ========== 会议室预定 1-101-005-000 ============
    ErrorCode MEETING_ROOM_BOOKING_NOT_EXISTS = new ErrorCode(1_101_005_000, "会议室预定申请单不存在");
    ErrorCode MEETING_ROOM_BOOKING_TIME_INVALID = new ErrorCode(1_101_005_001, "会议时间无效，请检查开始时间和结束时间");
    ErrorCode MEETING_ROOM_BOOKING_TIME_PAST = new ErrorCode(1_101_005_002, "会议开始时间不能是过去时间");
    ErrorCode MEETING_ROOM_BOOKING_TIME_CONFLICT = new ErrorCode(1_101_005_003, "该时间段会议室已被预定，请选择其他时间");
    ErrorCode MEETING_ROOM_BOOKING_CANNOT_DELETE = new ErrorCode(1_101_005_004, "只能删除草稿状态的预定申请单");

    // ========== 合同审批 1-101-006-000 ============
    ErrorCode CONTRACT_BILL_NOT_EXISTS = new ErrorCode(1_101_006_000, "合同审批单不存在");

    // ========== 公文发文 1-101-007-000 ============
    ErrorCode DOCUMENT_DISPATCH_BILL_NOT_EXISTS = new ErrorCode(1_101_007_000, "公文发文单不存在");

    // ========== 费用报销 1-101-008-000 ============
    ErrorCode EXPENSE_REIMBURSE_BILL_NOT_EXISTS = new ErrorCode(1_101_008_000, "费用报销单不存在");
    ErrorCode EXPENSE_TRAVELER_COUNT_REQUIRED = new ErrorCode(1_101_008_001, "差旅报销人数不能为空且须大于等于1");

    // ========== 项目立项 1-101-009-000 ============
    ErrorCode PROJECT_INITIATION_BILL_NOT_EXISTS = new ErrorCode(1_101_009_000, "项目立项单不存在");

    // ========== 收文办理 1-101-010-000 ============
    ErrorCode INCOMING_DOCUMENT_BILL_NOT_EXISTS = new ErrorCode(1_101_010_000, "收文办理单不存在");

    // ========== 差旅申请 1-101-011-000 ============
    ErrorCode TRAVEL_APPLY_BILL_NOT_EXISTS = new ErrorCode(1_101_011_000, "差旅申请单不存在");
    ErrorCode TRAVEL_END_DATE_INVALID = new ErrorCode(1_101_011_001, "结束日期不能早于开始日期");
    ErrorCode TRAVEL_APPLY_ALREADY_LINKED = new ErrorCode(1_101_011_002, "出差申请单已被其他差旅报销单关联");
    ErrorCode TRAVEL_COMPANION_REQUIRED = new ErrorCode(1_101_011_003, "同行人不能为空");
    ErrorCode TRAVEL_TRAVELER_COUNT_REQUIRED = new ErrorCode(1_101_011_004, "出行人数不能为空且须大于等于1");
    ErrorCode TRAVEL_DAYS_REQUIRED = new ErrorCode(1_101_011_005, "出差天数不能为空且不能小于0");

    // ========== 纠错管理 1-101-012-000 ============
    ErrorCode CORRECTION_BILL_NOT_EXISTS = new ErrorCode(1_101_012_000, "纠错申请单不存在");
    ErrorCode CORRECTION_SOURCE_BILL_NOT_EXISTS = new ErrorCode(1_101_012_001, "纠错原单据不存在");
    ErrorCode CORRECTION_BILL_ALREADY_IN_PROGRESS = new ErrorCode(1_101_012_002, "该单据已有进行中的纠错");
    ErrorCode CORRECTION_SOURCE_BILL_NOT_APPROVED = new ErrorCode(1_101_012_003, "仅已审批通过的单据可发起纠错");
    ErrorCode CORRECTION_BILL_TYPE_NOT_SUPPORTED = new ErrorCode(1_101_012_004, "不支持的单据类型");
    ErrorCode CORRECTION_COUNCIL_RESULT_REQUIRED = new ErrorCode(1_101_012_005, "理事会决议须填写处理结果");
    ErrorCode CORRECTION_COUNCIL_FILE_REQUIRED = new ErrorCode(1_101_012_006, "理事会决议须上传决议文件");
    ErrorCode CORRECTION_BILL_FROZEN = new ErrorCode(1_101_012_007, "单据已冻结，暂不可执行该操作");

    // ========== 套红模板 1-101-013-000 ============
    ErrorCode RED_TEMPLATE_NOT_EXISTS = new ErrorCode(1_101_013_000, "套红模板不存在");

    // ========== 日常报销 1-101-014-000 ============
    ErrorCode DAILY_EXPENSE_BILL_NOT_EXISTS = new ErrorCode(1_101_014_000, "日常报销单不存在");

    // ========== 费用支出申请 1-101-015-000 ============
    ErrorCode EXPENSE_PAYMENT_BILL_NOT_EXISTS = new ErrorCode(1_101_015_000, "费用支出申请单不存在");
    ErrorCode EXPENSE_PAYMENT_BILL_DETAIL_REQUIRED = new ErrorCode(1_101_015_001, "请至少添加一条费用明细");

    // ========== 接待申请单 1-101-016-000 ============
    ErrorCode RECEPTION_APPLY_BILL_NOT_EXISTS = new ErrorCode(1_101_016_000, "接待申请单不存在");

    // ========== 审批可见范围 1-101-017-000 ============
    ErrorCode OA_BILL_VIEW_DENIED = new ErrorCode(1_101_017_000, "无权查看该单据");
}
