package cn.dh.oa.module.oa.dal.dataobject.template;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;

/**
 * 套红模板 DO
 *
 * @author 鼎衡
 */
@TableName("oa_red_template")
@KeySequence("oa_red_template_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedTemplateDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 机关/公司名称（红头大字）
     */
    private String orgName;
    /**
     * 名称字号（红头大字字号，默认36）
     */
    private Integer nameFontSize;
    /**
     * 字号前缀（如：无办发）
     */
    private String docNumberPrefix;
    /**
     * 印章图片URL
     */
    private String sealImage;
    /**
     * 分隔线样式（single=单线 double=双线）
     */
    private String separatorStyle;
    /**
     * 状态（0正常 1停用）
     */
    private Integer status;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 备注
     */
    private String remark;

}
