package org.ilms.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.ilms.repository.CaseRepository;
import org.ilms.util.CommonUtils;
import org.ilms.util.ILMSConstants;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.web.model.Case;
import org.ilms.web.model.CaseRequest;
import org.ilms.web.model.CaseSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
@Slf4j
public class CaseValidator {
    @Autowired
    CommonUtils commonUtils;

    @Autowired
    CaseRepository caseRepository;

    private static Map<String, String> validateCodes(Case cases, Map<String, List<String>> codes, Map<String, String> errorMap) {
        if (Objects.nonNull(cases.getCourt())) {
            if (Objects.nonNull(cases.getCourt().getCourtName()) && !codes.get(ILMSConstants.MDMS_ILMS_COURT_NAME).contains(cases.getCourt().getCourtName())) {
                errorMap.put("Invalid CourtName", "The CourtName '" + cases.getCourt().getCourtName() + "' does not exists");
            }
        }
        if (Objects.nonNull(cases.getType()) && !codes.get(ILMSConstants.MDMS_ILMS_CASE_TYPE).contains(cases.getType())) {
            errorMap.put("Invalid CASE TYPE", "The CaseType '" + cases.getType() + "' does not exists");
        }
        if (Objects.nonNull(cases.getCaseStatus()) && !codes.get(ILMSConstants.MDMS_ILMS_CASE_STATUS).contains(cases.getCaseStatus())) {
            errorMap.put("Invalid CASE Status", "The CaseStatus '" + cases.getCaseStatus() + "' does not exists");
        }
        if (Objects.nonNull(cases.getCategory()) && !codes.get(ILMSConstants.MDMS_ILMS_CASE_CATEGORY).contains(cases.getCategory())) {
            errorMap.put("Invalid CaseCategory", "The CaseCategory '" + cases.getCategory() + "' does not exists");
        }
        if (Objects.nonNull(cases.getStage()) && !codes.get(ILMSConstants.MDMS_ILMS_CASE_STAGE).contains(cases.getStage())) {
            errorMap.put("Invalid CaseStage", "The CaseStage '" + cases.getStage() + "' does not exists");
        }
        if (Objects.nonNull(cases.getSubStage()) && !codes.get(ILMSConstants.MDMS_ILMS_SUB_STAGE).contains(cases.getSubStage())) {
            errorMap.put("Invalid CaseSubStage", "The CaseSubStage '" + cases.getSubStage() + "' does not exists");
        }
        if (Objects.nonNull(cases.getRecommendOIC()) && !codes.get(ILMSConstants.MDMS_ILMS_DEPARTMENT_IOC).contains(cases.getRecommendOIC())) {
            errorMap.put("Invalid RecommendOIC", "The RecommendOIC '" + cases.getRecommendOIC() + "' does not exists");
        }
        if (Objects.nonNull(cases.getPriority()) && !codes.get(ILMSConstants.CASE_FLAG).contains(cases.getPriority())) {
            errorMap.put("Invalid CaseFlag", "The CaseFlag '" + cases.getPriority() + "' does not exists");
        }
        if (Objects.nonNull(cases.getDocuments())) {
            cases.getDocuments().forEach(document -> {
                if (Objects.nonNull(document.getDocumentType()) && !codes.get(ILMSConstants.MDMS_ILMS_DOCUMENT_CATEGORY)
                        .contains(document.getDocumentType())) {
                    errorMap.put("Invalid DocumentCategory", "The DocumentCategory '" + document.getDocumentType() + "' does not exists");
                }
            });
        }
        return errorMap;

    }

    public void validateCreate(CaseRequest caseRequest) {

        if (!StringUtils.isNotBlank(caseRequest.getCaseObj().getTenantId())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "TenantId is mandatory [ " + caseRequest.getCaseObj().getTenantId() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCaseObj().getNumber())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseNumber is mandatory [ " + caseRequest.getCaseObj().getNumber() + " ]");
        }

        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(caseRequest.getCaseObj(), caseRequest, errorMap);
    }

    public void validateUpdate(Case aCase, CaseRequest caseRequest) {
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(aCase, caseRequest, errorMap);
    }

    private void validateMasterData(Case aCase, CaseRequest request, Map<String, String> errorMap) {

        String tenantId = aCase.getTenantId();

        List<String> masterNames = new ArrayList<>(
                Arrays.asList(ILMSConstants.MDMS_ILMS_CASE_TYPE, ILMSConstants.MDMS_ILMS_CASE_STATUS, ILMSConstants.MDMS_ILMS_CASE_CATEGORY,
                        ILMSConstants.MDMS_ILMS_CASE_STAGE, ILMSConstants.MDMS_ILMS_SUB_STAGE, ILMSConstants.MDMS_ILMS_GENDER_TYPE,
                        ILMSConstants.MDMS_ILMS_PETITIONER_TYPE, ILMSConstants.MDMS_ILMS_DEPARTMENT_NAME, ILMSConstants.MDMS_ILMS_DOCUMENT_CATEGORY,
                        ILMSConstants.MDMS_ILMS_DEPARTMENT_IOC, ILMSConstants.CASE_FLAG, ILMSConstants.MDMS_ILMS_COURT_NAME));

        Map<String, List<String>> codes = commonUtils.getAttributeValues(tenantId, ILMSConstants.MDMS_ILMS_MOD_NAME, masterNames, "$.*.code",
                ILMSConstants.JSONPATH_CODES, request.getRequestInfo());

        if (null != codes) {
            validateMDMSData(masterNames, codes);
            validateCodes(aCase, codes, errorMap);
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

    public void caseNumberDuplicacyCheck(CaseRequest caseRequest) {
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().number(Collections.singletonList(caseRequest.getCaseObj().getNumber()))
                .build();
        Integer count = caseRepository.getCaseCount(criteria);
        if (count >= 1) {
            throw new CustomException(ILMSErrorConstants.DUPLICATE_VALUE_ERROR,
                    "Already Exists In System, case number should be unique [ " + caseRequest.getCaseObj().getNumber() + " ]");
        }
    }

}
