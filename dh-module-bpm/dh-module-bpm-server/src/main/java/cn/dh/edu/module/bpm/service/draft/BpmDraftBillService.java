package cn.dh.edu.module.bpm.service.draft;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.common.util.object.PageUtils;
import cn.dh.edu.module.bpm.controller.admin.draft.vo.BpmDraftBillPageReqVO;
import cn.dh.edu.module.bpm.controller.admin.draft.vo.BpmDraftBillRespVO;
import cn.dh.edu.module.bpm.dal.dataobject.definition.BpmCategoryDO;
import cn.dh.edu.module.bpm.dal.dataobject.definition.BpmProcessDefinitionInfoDO;
import cn.dh.edu.module.bpm.framework.flowable.core.util.FlowableUtils;
import cn.dh.edu.module.bpm.service.definition.BpmCategoryService;
import cn.dh.edu.module.bpm.service.definition.BpmProcessDefinitionService;
import cn.dh.edu.module.hrm.api.bill.HrmDraftBillApi;
import cn.dh.edu.module.hrm.api.bill.dto.HrmDraftBillQueryDTO;
import cn.dh.edu.module.hrm.api.bill.dto.HrmDraftBillRespDTO;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 草稿箱：聚合 HRM 未提交单据
 */
@Service
public class BpmDraftBillService {

    @Resource
    private HrmDraftBillApi hrmDraftBillApi;
    @Resource
    private BpmProcessDefinitionService processDefinitionService;
    @Resource
    private BpmCategoryService categoryService;
    @Resource
    private RepositoryService repositoryService;

    public PageResult<BpmDraftBillRespVO> getMyDraftBillPage(Long userId, BpmDraftBillPageReqVO pageReqVO) {
        HrmDraftBillQueryDTO query = buildQuery(pageReqVO);
        List<BpmDraftBillRespVO> all = new ArrayList<>();
        hrmDraftBillApi.listMyDraftBills(userId, query).forEach(draft -> all.add(convert(draft)));

        all.sort(Comparator.comparing(BpmDraftBillRespVO::getCreateTime,
                Comparator.nullsLast(Comparator.reverseOrder())));

        int total = all.size();
        if (total == 0) {
            return PageResult.empty(0L);
        }
        int start = PageUtils.getStart(pageReqVO);
        if (start >= total) {
            return new PageResult<>(List.of(), (long) total);
        }
        int end = Math.min(start + pageReqVO.getPageSize(), total);
        return new PageResult<>(all.subList(start, end), (long) total);
    }

    private HrmDraftBillQueryDTO buildQuery(BpmDraftBillPageReqVO pageReqVO) {
        HrmDraftBillQueryDTO query = new HrmDraftBillQueryDTO();
        query.setBillCode(pageReqVO.getBillCode());
        query.setProcessDefinitionKey(pageReqVO.getBillType());
        query.setCompanyId(pageReqVO.getCompanyId());
        query.setDeptId(pageReqVO.getDeptId());
        Set<String> keys = resolveProcessDefinitionKeys(pageReqVO.getCategory(), pageReqVO.getBillType());
        if (keys != null) {
            query.setProcessDefinitionKeys(keys);
        }
        if (ArrayUtil.isNotEmpty(pageReqVO.getCreateTime())) {
            query.setCreateTimeStart(pageReqVO.getCreateTime()[0]);
            query.setCreateTimeEnd(pageReqVO.getCreateTime()[1]);
        }
        return query;
    }

    private Set<String> resolveProcessDefinitionKeys(String category, String billType) {
        if (StrUtil.isNotBlank(billType)) {
            return null;
        }
        if (StrUtil.isBlank(category)) {
            return null;
        }
        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery()
                .processDefinitionTenantId(FlowableUtils.getTenantId())
                .processDefinitionCategory(category)
                .latestVersion()
                .list();
        if (CollUtil.isEmpty(definitions)) {
            return Set.of();
        }
        return definitions.stream().map(ProcessDefinition::getKey).collect(Collectors.toSet());
    }

    private BpmDraftBillRespVO convert(HrmDraftBillRespDTO draft) {
        return buildVo(draft.getBillId(), draft.getBillCode(), draft.getProcessDefinitionKey(),
                draft.getSummary(), draft.getCreateTime(), draft.getDeptName());
    }

    private final Map<String, ProcessMeta> processMetaCache = new HashMap<>();

    private BpmDraftBillRespVO buildVo(Long billId, String billCode, String processDefinitionKey,
                                     String summary, java.time.LocalDateTime createTime, String deptName) {
        BpmDraftBillRespVO vo = new BpmDraftBillRespVO();
        vo.setBillId(billId);
        vo.setBillCode(billCode);
        vo.setProcessDefinitionKey(processDefinitionKey);
        vo.setSummary(summary);
        vo.setCreateTime(createTime);
        vo.setDeptName(deptName);

        ProcessMeta meta = getProcessMeta(processDefinitionKey);
        vo.setBillTypeName(meta.name);
        vo.setCategory(meta.category);
        vo.setCategoryName(meta.categoryName);
        vo.setInfoPath(meta.infoPath);
        return vo;
    }

    private ProcessMeta getProcessMeta(String processDefinitionKey) {
        return processMetaCache.computeIfAbsent(processDefinitionKey, key -> {
            ProcessDefinition definition = processDefinitionService.getActiveProcessDefinition(key);
            if (definition == null) {
                return new ProcessMeta(key, null, null, null);
            }
            BpmProcessDefinitionInfoDO info = processDefinitionService.getProcessDefinitionInfo(definition.getId());
            String infoPath = info != null ? info.getFormCustomCreatePath() : null;
            String categoryName = null;
            if (StrUtil.isNotBlank(definition.getCategory())) {
                Map<String, BpmCategoryDO> categoryMap = categoryService.getCategoryMap(Set.of(definition.getCategory()));
                BpmCategoryDO category = categoryMap.get(definition.getCategory());
                categoryName = category != null ? category.getName() : null;
            }
            return new ProcessMeta(definition.getName(), definition.getCategory(), categoryName, infoPath);
        });
    }

    private record ProcessMeta(String name, String category, String categoryName, String infoPath) {
    }

}
