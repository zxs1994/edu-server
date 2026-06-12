package cn.dh.oa.module.oa.service.template;

import java.util.*;
import jakarta.validation.*;
import cn.dh.oa.module.oa.controller.admin.template.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.template.RedTemplateDO;
import cn.dh.oa.framework.common.pojo.PageResult;

/**
 * 套红模板 Service 接口
 *
 * @author 鼎衡
 */
public interface RedTemplateService {

    /**
     * 创建套红模板
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createRedTemplate(@Valid RedTemplateSaveReqVO createReqVO);

    /**
     * 更新套红模板
     *
     * @param updateReqVO 更新信息
     */
    void updateRedTemplate(@Valid RedTemplateSaveReqVO updateReqVO);

    /**
     * 删除套红模板
     *
     * @param id 编号
     */
    void deleteRedTemplate(Long id);

    /**
     * 批量删除套红模板
     *
     * @param ids 编号列表
     */
    void deleteRedTemplateListByIds(List<Long> ids);

    /**
     * 获得套红模板
     *
     * @param id 编号
     * @return 套红模板
     */
    RedTemplateDO getRedTemplate(Long id);

    /**
     * 获得所有启用的套红模板
     *
     * @return 套红模板列表
     */
    List<RedTemplateDO> getRedTemplateList();

    /**
     * 获得套红模板分页
     *
     * @param pageReqVO 分页查询
     * @return 套红模板分页
     */
    PageResult<RedTemplateDO> getRedTemplatePage(RedTemplatePageReqVO pageReqVO);

}
