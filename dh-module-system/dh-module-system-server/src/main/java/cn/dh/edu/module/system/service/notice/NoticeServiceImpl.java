package cn.dh.edu.module.system.service.notice;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.framework.common.enums.CommonStatusEnum;
import cn.dh.edu.module.system.controller.admin.notice.vo.NoticePageReqVO;
import cn.dh.edu.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
import cn.dh.edu.module.system.dal.dataobject.notice.NoticeDO;
import cn.dh.edu.module.system.dal.mysql.notice.NoticeMapper;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.module.system.enums.ErrorCodeConstants.NOTICE_NOT_FOUND;
import static cn.dh.edu.module.system.enums.ErrorCodeConstants.NOTICE_DISABLED;

/**
 * 通知公告 Service 实现类
 *
 * @author 鼎衡
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeMapper noticeMapper;

    @Override
    public Long createNotice(NoticeSaveReqVO createReqVO) {
        NoticeDO notice = BeanUtils.toBean(createReqVO, NoticeDO.class);
        noticeMapper.insert(notice);
        return notice.getId();
    }

    @Override
    public void updateNotice(NoticeSaveReqVO updateReqVO) {
        // 校验是否存在
        validateNoticeExists(updateReqVO.getId());
        // 更新通知公告
        NoticeDO updateObj = BeanUtils.toBean(updateReqVO, NoticeDO.class);
        noticeMapper.updateById(updateObj);
    }

    @Override
    public void deleteNotice(Long id) {
        // 校验是否存在
        validateNoticeExists(id);
        // 删除通知公告
        noticeMapper.deleteById(id);
    }

    @Override
    public void deleteNoticeList(List<Long> ids) {
        noticeMapper.deleteByIds(ids);
    }

    @Override
    public PageResult<NoticeDO> getNoticePage(NoticePageReqVO reqVO) {
        return noticeMapper.selectPage(reqVO);
    }

    @Override
    public NoticeDO getNotice(Long id) {
        return noticeMapper.selectById(id);
    }

    @Override
    public NoticeDO pushNotice(Long id) {
        NoticeDO notice = validateNoticeExists(id);
        if (CommonStatusEnum.isDisable(notice.getStatus())) {
            throw exception(NOTICE_DISABLED);
        }
        return notice;
    }


    @VisibleForTesting
    public NoticeDO validateNoticeExists(Long id) {
        if (id == null) {
            throw exception(NOTICE_NOT_FOUND);
        }
        NoticeDO notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw exception(NOTICE_NOT_FOUND);
        }
        return notice;
    }

}
