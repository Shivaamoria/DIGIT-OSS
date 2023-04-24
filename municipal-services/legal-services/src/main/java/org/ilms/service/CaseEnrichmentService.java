package org.ilms.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.repository.IdGenRepository;
import org.ilms.util.CaseUtils;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.web.model.*;
import org.ilms.web.model.enums.PartyType;
import org.ilms.web.model.enums.Status;
import org.ilms.web.model.idGen.IdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CaseEnrichmentService {
    @Autowired
    private ILMSConfiguration ilmsConfiguration;

    @Autowired
    private CaseUtils caseUtils;

    @Autowired
    private IdGenRepository idGenRepository;

    public void enrichCaseCreateRequest(CaseRequest caseRequest) {
        RequestInfo requestInfo = caseRequest.getRequestInfo();
        Case aCase = caseRequest.getCaseObj();
        setIdgenIds(caseRequest);
        AuditDetails auditDetails = caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
        caseRequest.getCaseObj().setAuditDetails(auditDetails);
        aCase.setAuditDetails(auditDetails);
        if (Objects.nonNull(caseRequest.getCaseObj().getCourt())) {
            caseRequest.getCaseObj().getCourt().setAuditDetails(auditDetails);
            aCase.getCourt().setAuditDetails(auditDetails);
        }
        for (Party party : aCase.getParties()) {
            party.setAuditDetails(auditDetails);
            party.getAdvocate().setAuditDetails(auditDetails);
        }
        if (!CollectionUtils.isEmpty(aCase.getDocuments())) {
            aCase.getDocuments().forEach(doc -> {
                doc.setAuditDetails(auditDetails);
                doc.setStatus(Status.ACTIVE);
            });
        }
        if (caseRequest.getCaseObj().getAct() != null) {
            caseRequest.getCaseObj().getAct().setAuditDetails(auditDetails);
            aCase.getAct().setAuditDetails(auditDetails);
        }
    }

    public void enrichmentForHearingUpdateRequest(HearingRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        Hearing ilmsCase = request.getHearing();
        AuditDetails auditDetails = caseUtils.getAuditDetails(request.getRequestInfo().getUserInfo().getUuid(), false);
        request.getHearing().setAuditDetails(auditDetails);
        ilmsCase.setAuditDetails(auditDetails);
        for (Party party : ilmsCase.getParties()) {
            party.setAuditDetails(auditDetails);
            party.getAdvocate().setAuditDetails(auditDetails);
        }
        if (request.getHearing().getPayment() != null) {
            request.getHearing().getPayment().setAuditDetails(auditDetails);
            ilmsCase.getPayment().setAuditDetails(auditDetails);
        }
    }

    private void setIdgenIds(CaseRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = request.getCaseObj().getTenantId();
        Case caseObj = request.getCaseObj();
        List<String> caseId = getIdList(requestInfo, tenantId, ilmsConfiguration.getCaseIdgenName(), ilmsConfiguration.getCaseIdgenFormat(), 1);
        ListIterator<String> caseItr = caseId.listIterator();
        List<String> actId = getIdList(requestInfo, tenantId, ilmsConfiguration.getActIdgenName(), ilmsConfiguration.getActIdgenFormat(), 1);
        ListIterator<String> actItr = actId.listIterator();
        List<String> courtId = getIdList(requestInfo, tenantId, ilmsConfiguration.getCourtIdgenName(),
                ilmsConfiguration.getCourtIdgenFormat(), 1);
        ListIterator<String> courtItr = courtId.listIterator();
        Map<String, String> errorMap = new HashMap<>();

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        caseObj.setId(caseItr.next());
        if (Objects.nonNull(caseObj.getCourt())) {
            caseObj.getCourt().setId(courtItr.next());
        }
        if (Objects.nonNull(caseObj.getAct())) {
            caseObj.getAct().setId(actItr.next());
        } else {
            Act act = new Act();
            act.setId(actItr.next());
            act.setStatus(Status.DRAFTED);
            caseObj.setAct(act);
        }
        for (Party party : caseObj.getParties()) {
            if (party.equals(PartyType.PETITIONER.toString())) {
                List<String> petitionerId = getIdList(requestInfo, tenantId, ilmsConfiguration.getPetitionerIdgenName(),
                        ilmsConfiguration.getPetitionerIdgenFormat(), 1);
                ListIterator<String> petitionerItr = petitionerId.listIterator();

                party.setId(petitionerItr.next());
                if (Objects.nonNull(party.getAdvocate())) {
                    List<String> padvocateId = getIdList(requestInfo, tenantId, ilmsConfiguration.getPetitionerAdvocateIdgenName(),
                            ilmsConfiguration.getPetitionerAdvocateIdgenFormat(), 1);
                    ListIterator<String> padvocateItr = padvocateId.listIterator();
                    party.getAdvocate().setId(padvocateItr.next());
                }
            } else {
                List<String> respondentId = getIdList(requestInfo, tenantId, ilmsConfiguration.getRespondentIdgenName(),
                        ilmsConfiguration.getRespondentIdgenFormat(), 1);
                ListIterator<String> respondentItr = respondentId.listIterator();
                party.setId(respondentItr.next());
                if (Objects.nonNull(party.getAdvocate())) {
                    List<String> radvocateId = getIdList(requestInfo, tenantId, ilmsConfiguration.getRespondentAdvocateIdgenName(),
                            ilmsConfiguration.getRespondentAdvocateIdgenFormat(), 1);
                    ListIterator<String> radvocateItr = radvocateId.listIterator();
                    party.getAdvocate().setId(radvocateItr.next());
                }
            }
        }
        if (Objects.nonNull(caseObj.getDocuments())) {
            caseObj.getDocuments().forEach((doc -> {
                List<String> docId = getIdList(requestInfo, tenantId, ilmsConfiguration.getDocumentIdgenName(),
                        ilmsConfiguration.getDocumentIdgenFormat(), 1);
                doc.setId(docId.get(0));
            }));
        }
    }

    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idName, idformat, count).getIdResponses();
        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException(ILMSErrorConstants.IDGEN_ERROR, "No ids returned from idgen Service");
        }
        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }

    public void enrichCaseUpdateRequest(CaseRequest caseRequest) {
        RequestInfo requestInfo = caseRequest.getRequestInfo();
        Case aCase = caseRequest.getCaseObj();
        AuditDetails auditDetails = caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), false);
        caseRequest.getCaseObj().setAuditDetails(auditDetails);
        aCase.setAuditDetails(auditDetails);
        if (caseRequest.getCaseObj().getCourt() != null) {
            caseRequest.getCaseObj().getCourt().setAuditDetails(auditDetails);
            aCase.getCourt().setAuditDetails(auditDetails);
        }
        for (Party party : aCase.getParties()) {
            party.setAuditDetails(auditDetails);
            party.getAdvocate().setAuditDetails(auditDetails);
        }
        if (caseRequest.getCaseObj().getAct() != null) {
            caseRequest.getCaseObj().getAct().setAuditDetails(auditDetails);
            aCase.getAct().setAuditDetails(auditDetails);
        }
        if (!CollectionUtils.isEmpty(aCase.getDocuments())) {
            aCase.getDocuments().forEach(doc -> {
                doc.setAuditDetails(auditDetails);
                doc.setStatus(Status.ACTIVE);
            });
        }
    }

    
}
