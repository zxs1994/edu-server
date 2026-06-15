package cn.dh.oa.module.oa.enums;

/**
 * OA 模块流程变量常量
 * 
 * 定义OA模块中各种业务流程的变量名称，用于在BPM流程中传递业务数据
 * 
 * @author 鼎衡
 */
public interface OaProcessVariableConstants {

    // ========== 用印申请单流程变量 ==========

    /**
     * 用印方式变量名
     * 
     * 用于在用印申请单流程中传递用印方式信息
     * 1-现场用章，2-借用印章
     */
    String PV_SEAL_USE_MODE = "sealUseMode";

    // ========== 会议室预定申请单流程变量 ==========

    /**
     * 预定需审批变量名
     * 
     * 用于在会议室预定申请单流程中传递是否需要审批信息
     * true-需要审批，false-不需要审批
     */
    String PV_MEETING_ROOM_NEED_APPROVAL = "meetingRoomNeedApproval";

    // ========== 合同审批单流程变量 ==========

    /**
     * 合同是否重大变量名
     *
     * 用于在合同审批单流程中传递是否为重大合同信息
     * true-重大合同，false-普通合同
     */
    String PV_CONTRACT_IS_MAJOR = "contractIsMajor";

    // ========== 公文发文单流程变量 ==========

    /**
     * 公文是否重要变量名
     *
     * 用于在公文发文单流程中传递是否为重要公文信息
     * true-重要公文，false-普通公文
     */
    String PV_DOC_IS_IMPORTANT = "docIsImportant";

    // ========== 项目立项单流程变量 ==========

    /**
     * 项目是否重大变量名
     *
     * 用于在项目立项单流程中传递是否为重大项目信息
     * true-重大项目，false-普通项目
     */
    String PV_PROJECT_IS_MAJOR = "projectIsMajor";

    // ========== 收文办理单流程变量 ==========

    /**
     * 收文是否重要变量名
     *
     * 用于在收文办理单流程中传递是否为重要收文信息
     * true-重要收文，false-普通收文
     */
    String PV_INCOMING_IS_IMPORTANT = "incomingIsImportant";

    // ========== 差旅申请单流程变量 ==========

    /**
     * 差旅是否出国变量名
     *
     * 用于在差旅申请单流程中传递是否为出国差旅信息
     * true-出国差旅，false-国内差旅
     */
    String PV_TRAVEL_IS_OVERSEAS = "travelIsOverseas";


}
