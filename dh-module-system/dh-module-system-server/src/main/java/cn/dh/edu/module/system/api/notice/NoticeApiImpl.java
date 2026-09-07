package cn.dh.edu.module.system.api.notice;

import cn.dh.edu.framework.common.enums.CommonStatusEnum;
import cn.dh.edu.framework.common.enums.UserTypeEnum;
import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.framework.tenant.core.util.TenantUtils;
import cn.dh.edu.module.infra.api.websocket.WebSocketSenderApi;
import cn.dh.edu.module.system.api.notice.dto.NoticeCreateReqDTO;
import cn.dh.edu.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
import cn.dh.edu.module.system.dal.dataobject.notice.NoticeDO;
import cn.dh.edu.module.system.service.notice.NoticeService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.dh.edu.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class NoticeApiImpl implements NoticeApi {

    @Resource
    private NoticeService noticeService;
    @Resource
    private WebSocketSenderApi webSocketSenderApi;

    @Override
    public CommonResult<Long> createAndPushNotice(NoticeCreateReqDTO reqDTO) {
        Long tenantId = reqDTO.getTenantId();
        if (tenantId != null && tenantId > 0) {
            return success(TenantUtils.execute(tenantId, () -> doCreateAndPushNotice(reqDTO)));
        }
        return success(doCreateAndPushNotice(reqDTO));
    }

    private Long doCreateAndPushNotice(NoticeCreateReqDTO reqDTO) {
        NoticeSaveReqVO createReqVO = BeanUtils.toBean(reqDTO, NoticeSaveReqVO.class);
        if (createReqVO.getStatus() == null) {
            createReqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        }
        if (createReqVO.getIsImportant() == null) {
            createReqVO.setIsImportant(Boolean.FALSE);
        }

        Long noticeId = noticeService.createNotice(createReqVO, reqDTO.getCreator());
        if (!Boolean.FALSE.equals(reqDTO.getPush())) {
            NoticeDO notice = noticeService.pushNotice(noticeId);
            webSocketSenderApi.sendObject(UserTypeEnum.ADMIN.getValue(), "notice-push", notice);
        }
        return noticeId;
    }

}
