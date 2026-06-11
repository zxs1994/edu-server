-- 更新BPM流程模型中的表单路径（从3层改为2层）

-- 1. 更新 act_re_model 表中的 META_INFO_ (JSON字段中的formCustomCreatePath)
UPDATE act_re_model 
SET META_INFO_ = REPLACE(META_INFO_, '/oa/contract/contract-bill-info', '/oa/contract-bill-info')
WHERE KEY_ = 'oa_contract_bill';

UPDATE act_re_model 
SET META_INFO_ = REPLACE(META_INFO_, '/oa/document/document-dispatch-bill-info', '/oa/document-dispatch-info')
WHERE KEY_ = 'oa_document_dispatch_bill';

UPDATE act_re_model 
SET META_INFO_ = REPLACE(META_INFO_, '/oa/expense/expense-reimburse-bill-info', '/oa/expense-reimburse-info')
WHERE KEY_ = 'oa_expense_reimburse_bill';

UPDATE act_re_model 
SET META_INFO_ = REPLACE(META_INFO_, '/oa/project/project-initiation-bill-info', '/oa/project-initiation-info')
WHERE KEY_ = 'oa_project_initiation_bill';

UPDATE act_re_model 
SET META_INFO_ = REPLACE(META_INFO_, '/oa/incoming/incoming-document-bill-info', '/oa/incoming-document-info')
WHERE KEY_ = 'oa_incoming_document_bill';

UPDATE act_re_model 
SET META_INFO_ = REPLACE(META_INFO_, '/oa/travel/travel-apply-bill-info', '/oa/travel-apply-info')
WHERE KEY_ = 'oa_travel_apply_bill';

-- 2. 更新 bpm_process_definition_info 表中的 form_custom_create_path 列
UPDATE bpm_process_definition_info 
SET form_custom_create_path = '/oa/contract-bill-info'
WHERE form_custom_create_path = '/oa/contract/contract-bill-info';

UPDATE bpm_process_definition_info 
SET form_custom_create_path = '/oa/document-dispatch-info'
WHERE form_custom_create_path = '/oa/document/document-dispatch-bill-info';

UPDATE bpm_process_definition_info 
SET form_custom_create_path = '/oa/expense-reimburse-info'
WHERE form_custom_create_path = '/oa/expense/expense-reimburse-bill-info';

UPDATE bpm_process_definition_info 
SET form_custom_create_path = '/oa/project-initiation-info'
WHERE form_custom_create_path = '/oa/project/project-initiation-bill-info';

UPDATE bpm_process_definition_info 
SET form_custom_create_path = '/oa/incoming-document-info'
WHERE form_custom_create_path = '/oa/incoming/incoming-document-bill-info';

UPDATE bpm_process_definition_info 
SET form_custom_create_path = '/oa/travel-apply-info'
WHERE form_custom_create_path = '/oa/travel/travel-apply-bill-info';

-- 验证
SELECT KEY_, 
  SUBSTRING_INDEX(SUBSTRING_INDEX(META_INFO_, 'formCustomCreatePath', -1), ',', 1) as form_path
FROM act_re_model 
WHERE KEY_ LIKE 'oa_%';

SELECT process_definition_id, form_custom_create_path 
FROM bpm_process_definition_info 
WHERE form_custom_create_path LIKE '/oa/%' AND deleted = 0;
