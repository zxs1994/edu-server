package cn.dh.oa.module.system.service.logger;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.biz.system.logger.dto.OperateLogCreateReqDTO;
import cn.dh.oa.module.system.api.logger.dto.OperateLogPageReqDTO;
import cn.dh.oa.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import cn.dh.oa.module.system.dal.dataobject.logger.OperateLogDO;

/**
 * 操作日志 Service 接口
 *
 * @author 鼎衡
 */
public interface OperateLogService {

    /**
     * 记录操作日志
     *
     * @param createReqDTO 创建请求
     */
    void createOperateLog(OperateLogCreateReqDTO createReqDTO);

    /**
     * 获得操作日志
     *
     * @param id 编号
     * @return 操作日志
     */
    OperateLogDO getOperateLog(Long id);

    /**
     * 获得操作日志分页列表
     *
     * @param pageReqVO 分页条件
     * @return 操作日志分页列表
     */
    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO);

    /**
     * 获得操作日志分页列表
     *
     * @param pageReqVO 分页条件
     * @return 操作日志分页列表
     */
    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqDTO pageReqVO);

}
