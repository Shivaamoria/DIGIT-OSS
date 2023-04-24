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
import org.ilms.web.model.Hearing;
import org.ilms.web.model.HearingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
@Slf4j
public class HearingValidator {
    @Autowired
    CommonUtils commonUtils;

    @Autowired
    private CaseRepository caseRepository;

    private static Map<String, String> validateCodes(Hearing hearing, Map<String, List<String>> codes, Map<String, String> errorMap) {

        if (Objects.nonNull(hearing.getBench()) && !codes.get(ILMSConstants.MDMS_ILMS_BENCH).contains(hearing.getBench())) {
            errorMap.put("Invalid Bench", "The Bench '" + hearing.getBench() + "' does not exists");
        }
        if (Objects.nonNull(hearing.getHearingType()) && !codes.get(ILMSConstants.MDMS_ILMS_HEARING_TYPE).contains(hearing.getHearingType())) {
            errorMap.put("Invalid HearingType", "The Type '" + hearing.getHearingType() + "' does not exists");
        }

        return errorMap;
    }

    public void createValidator(HearingRequest hearingRequest) {

        if (!StringUtils.isNotBlank(hearingRequest.getHearing().getCaseId())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "CaseId is mandatory");
        }

        if (!StringUtils.isNotBlank(hearingRequest.getHearing().getCaseNumber())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "CaseNumber is mandatory");
        }

        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(hearingRequest.getHearing(), hearingRequest, errorMap);
    }

    public void updateValidator(Hearing hearing, HearingRequest hearingRequest) {
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(hearing, hearingRequest, errorMap);
    }

    private void validateMasterData(Hearing hearing, HearingRequest request, Map<String, String> errorMap) {

        String caseId = hearing.getCaseId();
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
        CaseResponse caseResponse = caseRepository.getILMSCaseData(criteria);
        String tenantId = caseResponse.getCaseList().get(0).getTenantId();

        List<String> masterNames = new ArrayList<>(
                Arrays.asList(ILMSConstants.MDMS_ILMS_COURT_NAME, ILMSConstants.MDMS_ILMS_DISTRICT, ILMSConstants.MDMS_ILMS_STATE,
                        ILMSConstants.MDMS_ILMS_BENCH, ILMSConstants.MDMS_ILMS_DIVISION, ILMSConstants.MDMS_ILMS_HEARING_TYPE));

        Map<String, List<String>> codes = commonUtils.getAttributeValues(tenantId, ILMSConstants.MDMS_ILMS_MOD_NAME, masterNames, "$.*.code",
                ILMSConstants.JSONPATH_CODES, request.getRequestInfo());

        if (null != codes) {
            validateMDMSData(masterNames, codes);
            validateCodes(hearing, codes, errorMap);
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
}
