package cn.dh.edu.module.bpm.service.oa.listener;

import cn.dh.edu.module.bpm.api.event.BpmProcessInstanceStatusEvent;
import cn.dh.edu.module.bpm.api.event.BpmProcessInstanceStatusEventListener;
import cn.dh.edu.module.bpm.service.oa.BpmOALeaveService;
import cn.dh.edu.module.bpm.service.oa.BpmOALeaveServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * OA 请假单的结果的监听器实现类
 *
 * @author 鼎衡
 */
@Component
public class BpmOALeaveStatusListener extends BpmProcessInstanceStatusEventListener {

    @Resource
    private BpmOALeaveService leaveService;

    @Override
    protected String getProcessDefinitionKey() {
        return BpmOALeaveServiceImpl.PROCESS_KEY;
    }

    @Override
    protected void onEvent(BpmProcessInstanceStatusEvent event) {
        leaveService.updateLeaveStatus(Long.parseLong(event.getBusinessKey()), event.getProcessInstanceInfo().getStatus());
    }

}
