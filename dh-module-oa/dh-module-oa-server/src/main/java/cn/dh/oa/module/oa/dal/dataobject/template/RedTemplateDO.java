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
     * 机关名称（红头大字）
     */
    private String orgName;
    /**
     * 文件类型标签（如：文 件、通 知）
     */
    private String docTypeLabel;
    /**
     * 红头颜色
     */
    private String headerColor;
    /**
     * 模板HTML内容
     */
    private String templateContent;
    /**
     * 预览图片URL
     */
    private String previewImage;
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
