package cn.dh.oa.module.oa.dal.mysql.project;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.oa.dal.dataobject.project.ProjectInitiationBillDO;
import cn.dh.oa.module.oa.service.bill.OaBillApprovalVisibleScope;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.oa.module.oa.controller.admin.project.vo.ProjectInitiationBillPageReqVO;

@Mapper
public interface ProjectInitiationBillMapper extends BaseMapperX<ProjectInitiationBillDO> {

default PageResult<ProjectInitiationBillDO> selectPageByVisibleScope(ProjectInitiationBillPageReqVO reqVO,
                                                        OaBillApprovalVisibleScope visibleScope) {
        LambdaQueryWrapperX<ProjectInitiationBillDO> wrapper = new LambdaQueryWrapperX<ProjectInitiationBillDO>()
                .likeIfPresent(ProjectInitiationBillDO::getBillCode, reqVO.getBillCode())
                .likeIfPresent(ProjectInitiationBillDO::getProjectName, reqVO.getProjectName())
                .eqIfPresent(ProjectInitiationBillDO::getProjectType, reqVO.getProjectType())
                .eqIfPresent(ProjectInitiationBillDO::getProcessStatus, reqVO.getProcessStatus())
                .eqIfPresent(ProjectInitiationBillDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(ProjectInitiationBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ProjectInitiationBillDO::getId);
        if (visibleScope != null) {
            visibleScope.apply(wrapper, ProjectInitiationBillDO::getCreator, ProjectInitiationBillDO::getProcessInstanceId);
        }
        return selectPage(reqVO, wrapper);
    }

}
