package cn.dh.edu.module.system.api.notice;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.module.system.api.notice.dto.NoticeCreateReqDTO;
import cn.dh.edu.module.system.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 通知公告")
public interface NoticeApi {

    String PREFIX = ApiConstants.PREFIX + "/notice";

    @PostMapping(PREFIX + "/create-and-push")
    @Operation(summary = "创建通知公告并推送给在线用户")
    CommonResult<Long> createAndPushNotice(@Valid @RequestBody NoticeCreateReqDTO reqDTO);

}
