package org.ilms.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.ilms.repository.CaseRepository;
import org.ilms.util.CommonUtils;
import org.ilms.util.ILMSConstants;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.web.model.CaseResponse;
import org.ilms.web.model.CaseSearchCriteria;
import org.ilms.web.model.JudgementRequest;
import org.ilms.web.model.judgement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
@Slf4j
public class JudgementValidator {
    @Autowired
    CommonUtils commonUtils;

    @Autowired
    private CaseRepository caseRepository;

    private static Map<String, String> validateCode(judgement judgement, Map<String, List<String>> codes, Map<String, String> errorMap) {
        if (judgement.getOrderType() != null && !codes.get(ILMSConstants.MDMS_ILMS_ORDER_TYPE).contains(judgement.getOrderType())) {
            errorMap.put("Invalid OrderType", "The OrderType '" + judgement.getOrderType() + "' does not exists");
        }
        return errorMap;
    }

    private static Map<String, String> validateCodesForUpdate(judgement judgement, Map<String, List<String>> codes, Map<String, String> errorMap) {

        if (judgement.getComplianceStatus() != null && !codes.get(ILMSConstants.MDMS_ILMS_STATUS_OF_COMPLIANCE)
                .contains(judgement.getComplianceStatus())) {
            errorMap.put("Invalid ComplianceStatus", "The ComplianceStatus '" + judgement.getComplianceStatus() + "' does not exists");
        }
        if (judgement.getOrderType() != null && !codes.get(ILMSConstants.MDMS_ILMS_ORDER_TYPE).contains(judgement.getOrderType())) {
            errorMap.put("Invalid OrderType", "The OrderType '" + judgement.getOrderType() + "' does not exists");
        }
        return errorMap;
    }

    public void createValidator(JudgementRequest request) {

        if (!StringUtils.isNotBlank(request.getJudgement().getCaseId())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "caseId is mandatory");
        }
        if (!StringUtils.isNotBlank(request.getJudgement().getOrderType())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "orderType is mandatory");
        }
        if (!StringUtils.isNotBlank(request.getJudgement().getOrderDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "orderDate is mandatory");
        }
        if (StringUtils.isNotBlank(request.getJudgement().getDecisionStatus())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "decisionStatus is not allowed while creating judgement");
        }
        if (!StringUtils.isNotBlank(request.getJudgement().getOrderNoOverride())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "orderNoOverride is mandatory");
        }
        if (!StringUtils.isNotBlank(request.getJudgement().getRevisedComplainceReason())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "revisedComplianceReason is mandatory");
        }
        if (StringUtils.isNotBlank(request.getJudgement().getComplianceStatus())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "complianceStatus is not allowed while creating judgement");
        }
        if (!StringUtils.isNotBlank(request.getJudgement().getRemarks())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "remarks is mandatory");
        }
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
    }

    public void updateValidator(judgement judgement, JudgementRequest request) {
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
    }

    private void validateMasterData(judgement judgement, JudgementRequest request, Map<String, String> errorMap) {

        String caseId = judgement.getCaseId();
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
        CaseResponse caseResponse = caseRepository.getILMSCaseData(criteria);
        String tenantId = caseResponse.getCaseList().get(0).getTenantId();
        List<String> masterNames = new ArrayList<>(Collections.singletonList(ILMSConstants.MDMS_ILMS_ORDER_TYPE));

        Map<String, List<String>> codes = commonUtils.getAttributeValues(tenantId, ILMSConstants.MDMS_ILMS_MOD_NAME, masterNames, "$.*.code",
                ILMSConstants.JSONPATH_CODES, request.getRequestInfo());

        if (null != codes) {
            validateMDMSData(masterNames, codes);
            validateCode(judgement, codes, errorMap);
        } else {
            errorMap.put("MASTER_FETCH_FAILED", "Couldn't fetch master data for validation");
        }

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
    }

    private void validateMDMSData(List<String> masterNames, Map<String, List<String>> codes) {

        Map<String, String> errorMap = new HashMap<>();
        for (String masterName : masterNames) {
            if (CollectionUtils.isEmpty(codes.get(masterName))) {
                errorMap.put("MDMS DATA ERROR ", "Unable to fetch " + masterName + " codes from MDMS");
            }
        }
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
    }

    private void validateMasterDataForUpdate(judgement judgement, JudgementRequest request, Map<String, String> errorMap) {

        String caseId = judgement.getCaseId();
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
        CaseResponse caseResponse = caseRepository.getILMSCaseData(criteria);
        String tenantId = caseResponse.getCaseList().get(0).getTenantId();
        List<String> masterNames = new ArrayList<>(Arrays.asList(ILMSConstants.MDMS_ILMS_STATUS_OF_COMPLIANCE, ILMSConstants.MDMS_ILMS_ORDER_TYPE));

        Map<String, List<String>> codes = commonUtils.getAttributeValues(tenantId, ILMSConstants.MDMS_ILMS_MOD_NAME, masterNames, "$.*.code",
                ILMSConstants.JSONPATH_CODES, request.getRequestInfo());

        if (null != codes) {
            validateMDMSData(masterNames, codes);
            validateCodesForUpdate(judgement, codes, errorMap);
        } else {
            errorMap.put("MASTER_FETCH_FAILED", "Couldn't fetch master data for validation");
        }

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
    }
}
