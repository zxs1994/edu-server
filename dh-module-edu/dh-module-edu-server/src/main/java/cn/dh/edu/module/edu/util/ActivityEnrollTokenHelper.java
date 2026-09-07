package cn.dh.edu.module.edu.util;

import cn.dh.edu.framework.dict.core.DictFrameworkUtils;
import cn.dh.edu.framework.tenant.core.util.TenantUtils;
import cn.dh.edu.framework.web.config.WebProperties;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityEnrollLinkDO;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityEnrollLinkMapper;
import cn.dh.edu.module.edu.enums.activity.EduActivityNoticeConstants;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.module.edu.enums.ErrorCodeConstants.ACTIVITY_INSTANCE_ENROLL_TOKEN_INVALID;

/**
 * 学生 H5 报名短链：落库 6 位码 → /e/{code}（不过期）
 */
@Slf4j
@Component
public class ActivityEnrollTokenHelper {

    private static final String H5_SHORT_PREFIX = "/e/";
    private static final int CODE_LEN = 6;
    private static final String CODE_ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int MAX_CODE_RETRY = 8;

    @Resource
    private WebProperties webProperties;
    @Resource
    private ActivityEnrollLinkMapper enrollLinkMapper;

    /**
     * 生成/复用落库短码
     */
    public String createShortCode(Long userId, Long instanceId, Long tenantId) {
        if (userId == null || instanceId == null || tenantId == null) {
            throw exception(ACTIVITY_INSTANCE_ENROLL_TOKEN_INVALID);
        }
        AtomicReference<String> codeRef = new AtomicReference<>();

        TenantUtils.execute(tenantId, () -> {
            ActivityEnrollLinkDO existing = enrollLinkMapper.selectByUserAndInstance(userId, instanceId);
            if (existing != null && StrUtil.isNotBlank(existing.getCode())) {
                codeRef.set(existing.getCode());
                return;
            }
            for (int i = 0; i < MAX_CODE_RETRY; i++) {
                String code = RandomUtil.randomString(CODE_ALPHABET, CODE_LEN);
                ActivityEnrollLinkDO row = ActivityEnrollLinkDO.builder()
                        .code(code)
                        .userId(userId)
                        .instanceId(instanceId)
                        .build();
                try {
                    enrollLinkMapper.insert(row);
                    codeRef.set(code);
                    return;
                } catch (DuplicateKeyException ex) {
                    log.warn("[createShortCode] 短码冲突重试，code={}", code);
                }
            }
            throw exception(ACTIVITY_INSTANCE_ENROLL_TOKEN_INVALID);
        });
        return codeRef.get();
    }

    /**
     * 解析落库短码
     */
    public Payload parseCredential(String credential) {
        if (StrUtil.isBlank(credential)) {
            throw exception(ACTIVITY_INSTANCE_ENROLL_TOKEN_INVALID);
        }
        String code = credential.trim();
        if (!code.matches("^[A-Za-z0-9]{" + CODE_LEN + "}$")) {
            throw exception(ACTIVITY_INSTANCE_ENROLL_TOKEN_INVALID);
        }
        AtomicReference<ActivityEnrollLinkDO> holder = new AtomicReference<>();
        TenantUtils.executeIgnore(() -> holder.set(enrollLinkMapper.selectByCode(code)));
        ActivityEnrollLinkDO link = holder.get();
        if (link == null) {
            throw exception(ACTIVITY_INSTANCE_ENROLL_TOKEN_INVALID);
        }
        Payload payload = new Payload();
        payload.setU(link.getUserId());
        payload.setI(link.getInstanceId());
        payload.setT(link.getTenantId());
        return payload;
    }

    public Payload parseToken(String token) {
        return parseCredential(token);
    }

    /**
     * {base}/e/{code}
     */
    public String buildEnrollUrl(Long userId, Long instanceId, Long tenantId) {
        String code = createShortCode(userId, instanceId, tenantId);
        String base = resolveH5BaseUrl();
        if (StrUtil.isBlank(base)) {
            return H5_SHORT_PREFIX + code;
        }
        return StrUtil.removeSuffix(base.trim(), "/") + H5_SHORT_PREFIX + code;
    }

    private String resolveH5BaseUrl() {
        try {
            String fromDict = DictFrameworkUtils.parseDictDataLabel(
                    EduActivityNoticeConstants.DICT_TYPE_ENROLL_CONFIG,
                    EduActivityNoticeConstants.DICT_VALUE_H5_BASE_URL);
            if (StrUtil.isNotBlank(fromDict)) {
                return fromDict.trim();
            }
        } catch (Exception ex) {
            log.warn("[resolveH5BaseUrl] 读取字典失败，回退 admin-ui.url", ex);
        }
        return webProperties.getAdminUi() != null ? webProperties.getAdminUi().getUrl() : null;
    }

    @Data
    public static class Payload {
        private Long u;
        private Long i;
        private Long t;
    }

}
