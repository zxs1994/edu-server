package cn.dh.oa.module.oa.service.template;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.dh.oa.module.oa.controller.admin.template.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.template.RedTemplateDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;

import cn.dh.oa.module.oa.dal.mysql.template.RedTemplateMapper;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.*;

/**
 * 套红模板 Service 实现类
 *
 * @author 鼎衡
 */
@Service
@Validated
public class RedTemplateServiceImpl implements RedTemplateService {

    @Resource
    private RedTemplateMapper redTemplateMapper;

    @Override
    public Long createRedTemplate(RedTemplateSaveReqVO createReqVO) {
        RedTemplateDO redTemplate = BeanUtils.toBean(createReqVO, RedTemplateDO.class);
        redTemplateMapper.insert(redTemplate);
        return redTemplate.getId();
    }

    @Override
    public void updateRedTemplate(RedTemplateSaveReqVO updateReqVO) {
        validateRedTemplateExists(updateReqVO.getId());
        RedTemplateDO updateObj = BeanUtils.toBean(updateReqVO, RedTemplateDO.class);
        redTemplateMapper.updateById(updateObj);
    }

    @Override
    public void deleteRedTemplate(Long id) {
        validateRedTemplateExists(id);
        redTemplateMapper.deleteById(id);
    }

    @Override
    public void deleteRedTemplateListByIds(List<Long> ids) {
        validateRedTemplateExists(ids);
        redTemplateMapper.deleteByIds(ids);
    }

    private void validateRedTemplateExists(Long id) {
        if (redTemplateMapper.selectById(id) == null) {
            throw exception(RED_TEMPLATE_NOT_EXISTS);
        }
    }

    private void validateRedTemplateExists(List<Long> ids) {
        List<RedTemplateDO> list = redTemplateMapper.selectByIds(ids);
        if (CollUtil.isEmpty(list) || list.size() != ids.size()) {
            throw exception(RED_TEMPLATE_NOT_EXISTS);
        }
    }

    @Override
    public RedTemplateDO getRedTemplate(Long id) {
        return redTemplateMapper.selectById(id);
    }

    @Override
    public List<RedTemplateDO> getRedTemplateList() {
        return redTemplateMapper.selectListByStatus(0);
    }

    @Override
    public PageResult<RedTemplateDO> getRedTemplatePage(RedTemplatePageReqVO pageReqVO) {
        return redTemplateMapper.selectPage(pageReqVO);
    }

}
