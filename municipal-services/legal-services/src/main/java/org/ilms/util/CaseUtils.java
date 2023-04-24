package org.ilms.util;

import org.egov.common.contract.request.User;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.repository.CaseRepository;
import org.ilms.service.CaseEnrichmentService;
import org.ilms.web.model.*;
import org.ilms.web.model.enums.CreationReason;
import org.ilms.web.model.enums.PartyType;
import org.ilms.web.model.workflow.ProcessInstance;
import org.ilms.web.model.workflow.ProcessInstanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class CaseUtils {
    @Autowired
    private CaseEnrichmentService caseEnrichmentService;

    @Autowired
    private ILMSConfiguration ilmsConfiguration;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private CommonUtils commonUtils;

    public AuditDetails getAuditDetails(String by, Boolean isCreate) {
        Long time = System.currentTimeMillis();
        if (isCreate) {
            return AuditDetails.builder().createdBy(by).createdTime(time).build();
        } else {
            return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
        }
    }

    public CaseRequest prepareObjectMapperForUpdate(Case oldData, CaseRequest caseRequest) {
        final CaseRequest request = new CaseRequest();

        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getTenantId())) {
            oldData.setTenantId(caseRequest.getCaseObj().getTenantId());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getNumber())) {
            oldData.setNumber(caseRequest.getCaseObj().getNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCnrNumber())) {
            oldData.setCnrNumber(caseRequest.getCaseObj().getCnrNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getParentCaseId())) {
            oldData.setParentCaseId(caseRequest.getCaseObj().getParentCaseId());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getLinkedCases())) {
            oldData.setLinkedCases(caseRequest.getCaseObj().getLinkedCases());
        }

        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getType())) {
            oldData.setType(caseRequest.getCaseObj().getType());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCategory())) {
            oldData.setCategory(caseRequest.getCaseObj().getCategory());
        }

        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getFilingNumber())) {
            oldData.setFilingNumber(caseRequest.getCaseObj().getFilingNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getFilingDate())) {
            oldData.setFilingDate(caseRequest.getCaseObj().getFilingDate());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRegistrationDate())) {
            oldData.setRegistrationDate(caseRequest.getCaseObj().getRegistrationDate());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getSummary())) {
            oldData.setSummary(caseRequest.getCaseObj().getSummary());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getArisingDetails())) {
            oldData.setArisingDetails(caseRequest.getCaseObj().getArisingDetails());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPolicyOrNonPolicyMatter())) {
            oldData.setPolicyOrNonPolicyMatter(caseRequest.getCaseObj().getPolicyOrNonPolicyMatter());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getApplicationNumber())) {
            oldData.setApplicationNumber(caseRequest.getCaseObj().getApplicationNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCaseStatus())) {
            oldData.setCaseStatus(caseRequest.getCaseObj().getCaseStatus());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getStage())) {
            oldData.setStage(caseRequest.getCaseObj().getStage());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getSubStage())) {
            oldData.setSubStage(caseRequest.getCaseObj().getSubStage());
        }
        if (Objects.nonNull(caseRequest.getCaseObj().getPriority())) {
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPriority())) {
                List<String> uuids = new ArrayList<>();
                uuids.add(caseRequest.getRequestInfo().getUserInfo().getUuid());
                if (commonUtils.isUserMO(uuids, caseRequest.getCaseObj().getTenantId(), "caseFlag")) {
                    oldData.setPriority(caseRequest.getCaseObj().getPriority());
                }
            }
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRecommendOIC())) {
            oldData.setRecommendOIC(caseRequest.getCaseObj().getRecommendOIC());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRemarks())) {
            oldData.setRemarks(caseRequest.getCaseObj().getRemarks());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getAdditionalDetails())) {
            oldData.setAdditionalDetails(caseRequest.getCaseObj().getAdditionalDetails());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getStatus())) {
            oldData.setStatus(caseRequest.getCaseObj().getStatus());
        }
        //        Setting Petitioner Details
        for (Party party : caseRequest.getCaseObj().getParties()) {
            for (Party oldParty : oldData.getParties()) {
                if (party.getPartyType().equals(PartyType.PETITIONER.toString()) && party.getId().equals(oldParty.getId())) {
                    if (Objects.nonNull(party.getDepartmentName())) {

                        if (!StringUtils.isEmpty(party.getCaseId())) {
                            oldParty.setCaseId(party.getCaseId());
                        }
                        if (!StringUtils.isEmpty(party.getFirstName())) {
                            oldParty.setFirstName(null);
                        }
                        if (!StringUtils.isEmpty(party.getLastName())) {
                            oldParty.setLastName(null);
                        }
                        if (!StringUtils.isEmpty(party.getGender())) {
                            oldParty.setGender(null);
                        }
                        if (!StringUtils.isEmpty(party.getPetitionerType())) {
                            oldParty.setPetitionerType(null);
                        }
                        if (!StringUtils.isEmpty(party.getAddress())) {
                            oldParty.setAddress(null);
                        }
                        if (!StringUtils.isEmpty(party.getDepartmentName())) {
                            oldParty.setDepartmentName(party.getDepartmentName());
                        }
                        if (!StringUtils.isEmpty(party.getContactNumber())) {
                            oldParty.setContactNumber(null);
                        }
                        if (!StringUtils.isEmpty(party.getPartyType())) {
                            oldParty.setPartyType(party.getPartyType());
                        }
                        if (!StringUtils.isEmpty(party.getStatus())) {
                            oldParty.setStatus(party.getStatus());
                        }
                        //Setting Data For Petitioner Advocate
                        if (!StringUtils.isEmpty(party.getAdvocate())) {
                            
                            if (!StringUtils.isEmpty(party.getAdvocate().getFirstName())) {
                                oldParty.getAdvocate().setFirstName(party.getAdvocate().getFirstName());
                            }
                            if (!StringUtils.isEmpty(party.getAdvocate().getLastName())) {
                                oldParty.getAdvocate().setLastName(party.getAdvocate().getLastName());
                            }
                            if (!StringUtils.isEmpty(party.getAdvocate().getContactNumber())) {
                                oldParty.getAdvocate().setContactNumber(party.getAdvocate().getContactNumber());
                            }

                            if (!StringUtils.isEmpty(party.getAdvocate().getStatus())) {
                                oldParty.getAdvocate().setStatus(party.getAdvocate().getStatus());
                            }
                        }

                    } else {
                        if (!StringUtils.isEmpty(party.getCaseId())) {
                            oldParty.setCaseId(party.getCaseId());
                        }
                        if (!StringUtils.isEmpty(party.getFirstName())) {
                            oldParty.setFirstName(party.getFirstName());
                        }
                        if (!StringUtils.isEmpty(party.getLastName())) {
                            oldParty.setLastName(party.getLastName());
                        }
                        if (!StringUtils.isEmpty(party.getGender())) {
                            oldParty.setGender(party.getGender());
                        }
                        if (!StringUtils.isEmpty(party.getPetitionerType())) {
                            oldParty.setPetitionerType(party.getPetitionerType());
                        }
                        if (!StringUtils.isEmpty(party.getAddress())) {
                            oldParty.setAddress(party.getAddress());
                        }
                        if (StringUtils.isEmpty(party.getDepartmentName())) {
                            oldParty.setDepartmentName(null);
                        }
                        if (!StringUtils.isEmpty(party.getContactNumber())) {
                            oldParty.setContactNumber(party.getContactNumber());
                        }
                        if (!StringUtils.isEmpty(party.getPartyType())) {
                            oldParty.setPartyType(party.getPartyType());
                        }
                        if (!StringUtils.isEmpty(party.getStatus())) {
                            oldParty.setStatus(party.getStatus());
                        }
                        //Setting Data For Petitioner Advocate
                        if (!StringUtils.isEmpty(party.getAdvocate())) {

                            if (!StringUtils.isEmpty(party.getAdvocate().getFirstName())) {
                                oldParty.getAdvocate().setFirstName(party.getAdvocate().getFirstName());
                            }
                            if (!StringUtils.isEmpty(party.getAdvocate().getLastName())) {
                                oldParty.getAdvocate().setLastName(party.getAdvocate().getLastName());
                            }
                            if (!StringUtils.isEmpty(party.getAdvocate().getContactNumber())) {
                                oldParty.getAdvocate().setContactNumber(party.getAdvocate().getContactNumber());
                            }
                            if (!StringUtils.isEmpty(party.getAdvocate().getStatus())) {
                                oldParty.getAdvocate().setStatus(party.getAdvocate().getStatus());
                            }
                        }
                    }
                }
                //       Setting Respondent details
                if (party.getPartyType().equals(PartyType.RESPONDENT.toString()) && party.getId().equals(oldParty.getId())) {
                    if (Objects.nonNull(party.getDepartmentName())) {

                        if (!StringUtils.isEmpty(party.getCaseId())) {
                            oldParty.setCaseId(party.getCaseId());
                        }
                        if (!StringUtils.isEmpty(party.getFirstName())) {
                            oldParty.setFirstName(null);
                        }
                        if (!StringUtils.isEmpty(party.getLastName())) {
                            oldParty.setLastName(null);
                        }
                        if (!StringUtils.isEmpty(party.getGender())) {
                            oldParty.setGender(null);
                        }
                        if (!StringUtils.isEmpty(party.getPetitionerType())) {
                            oldParty.setPetitionerType(null);
                        }
                        if (!StringUtils.isEmpty(party.getAddress())) {
                            oldParty.setAddress(null);
                        }
                        if (!StringUtils.isEmpty(party.getDepartmentName())) {
                            oldParty.setDepartmentName(party.getDepartmentName());
                        }
                        if (!StringUtils.isEmpty(party.getContactNumber())) {
                            oldParty.setContactNumber(null);
                        }
                        if (!StringUtils.isEmpty(party.getPartyType())) {
                            oldParty.setPartyType(party.getPartyType());
                        }
                        if (!StringUtils.isEmpty(party.getStatus())) {
                            oldParty.setStatus(party.getStatus());
                        }
                    } else {
                        if (!StringUtils.isEmpty(party.getCaseId())) {
                            oldParty.setCaseId(party.getCaseId());
                        }
                        if (!StringUtils.isEmpty(party.getFirstName())) {
                            oldParty.setFirstName(party.getFirstName());
                        }
                        if (!StringUtils.isEmpty(party.getLastName())) {
                            oldParty.setLastName(party.getLastName());
                        }
                        if (!StringUtils.isEmpty(party.getGender())) {
                            oldParty.setGender(party.getGender());
                        }
                        if (!StringUtils.isEmpty(party.getPetitionerType())) {
                            oldParty.setPetitionerType(party.getPetitionerType());
                        }
                        if (!StringUtils.isEmpty(party.getAddress())) {
                            oldParty.setAddress(party.getAddress());
                        }
                        if (StringUtils.isEmpty(party.getDepartmentName())) {
                            oldParty.setDepartmentName(null);
                        }
                        if (!StringUtils.isEmpty(party.getContactNumber())) {
                            oldParty.setContactNumber(party.getContactNumber());
                        }
                        if (!StringUtils.isEmpty(party.getPartyType())) {
                            oldParty.setPartyType(party.getPartyType());
                        }
                        if (!StringUtils.isEmpty(party.getStatus())) {
                            oldParty.setStatus(party.getStatus());
                        }
                    }
                    //Setting Data For Respondent Advocate
                    if (!StringUtils.isEmpty(party.getAdvocate())) {

                        if (!StringUtils.isEmpty(party.getAdvocate().getFirstName())) {
                            oldParty.getAdvocate().setFirstName(party.getAdvocate().getFirstName());
                        }
                        if (!StringUtils.isEmpty(party.getAdvocate().getLastName())) {
                            oldParty.getAdvocate().setLastName(party.getAdvocate().getLastName());
                        }

                        if (!StringUtils.isEmpty(party.getAdvocate().getContactNumber())) {
                            oldParty.getAdvocate().setContactNumber(party.getAdvocate().getContactNumber());
                        }
                        if (!StringUtils.isEmpty(party.getAdvocate().getStatus())) {
                            oldParty.getAdvocate().setStatus(party.getAdvocate().getStatus());
                        }
                    }
                }
            }
        }
        //setting act details
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getAct())) {
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getAct().getCaseId())) {
                oldData.getAct().setCaseId(caseRequest.getCaseObj().getAct().getCaseId());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getAct().getActName())) {
                oldData.getAct().setActName(caseRequest.getCaseObj().getAct().getActName());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getAct().getSectionNumber())) {
                oldData.getAct().setSectionNumber(caseRequest.getCaseObj().getAct().getSectionNumber());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getAct().getStatus())) {
                oldData.getAct().setStatus(caseRequest.getCaseObj().getAct().getStatus());
            }
        }
        //setting documents details
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getDocuments())) {
            List<Document> documentList = caseRequest.getCaseObj().getDocuments();
            for (Document document : documentList) {
                for (Document oldDocData : oldData.getDocuments()) {
                    //                    oldData.getDocuments().forEach(oldDocData -> {
                    if (oldDocData.getId().equalsIgnoreCase(document.getId())) {
                        if (!StringUtils.isEmpty(document.getCaseId())) {
                            oldDocData.setCaseId(document.getCaseId());
                        }
                        if (!StringUtils.isEmpty(document.getRemarks())) {
                            oldDocData.setRemarks(document.getRemarks());
                        }
                        if (!StringUtils.isEmpty(document.getDocumentId())) {
                            oldDocData.setDocumentId(document.getDocumentId());
                        }
                        if (!StringUtils.isEmpty(document.getDocumentType())) {
                            oldDocData.setDocumentType(document.getDocumentType());
                        }
                        if (!StringUtils.isEmpty(document.getFileStoreId())) {
                            oldDocData.setFileStoreId(document.getFileStoreId());
                        }
                        if (!StringUtils.isEmpty(document.getStatus())) {
                            oldDocData.setStatus(document.getStatus());
                        }
                    }
                }
            }
        }
        request.setCaseObj(oldData);
        request.setRequestInfo(caseRequest.getRequestInfo());
        caseEnrichmentService.enrichCaseUpdateRequest(request);
        return request;
    }

    public ProcessInstanceRequest getWfForCaseCreate(CaseRequest request, CreationReason creationReasonForWorkflow) {

        Case aCase = request.getCaseObj();
        ProcessInstance wf = null != aCase.getWorkflow() ? aCase.getWorkflow() : new ProcessInstance();
        wf.setBusinessId(aCase.getId());

        switch (creationReasonForWorkflow) {
            case CREATE:
                wf.setBusinessService(ilmsConfiguration.getCreatePTWfName());
                wf.setModuleName(ilmsConfiguration.getPropertyModuleName());

                wf.setAction("CREATE");
                wf.setTenantId(request.getCaseObj().getTenantId());
                List<User> userList = new ArrayList<>();
                User user = new User();
                user.setUuid(request.getRequestInfo().getUserInfo().getUuid());
                userList.add(user);
                wf.setAssignes(userList);

                break;

            case UPDATE:
                String caseId = request.getCaseObj().getId();
                CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
                CaseResponse caseResponse = caseRepository.getILMSCaseData(criteria);
                String tenantId = caseResponse.getCaseList().get(0).getTenantId();
                wf.setTenantId(tenantId);
                wf.setAssignes(request.getCaseObj().getWorkflow().getAssignes());
                break;

            default:
                break;
        }
        aCase.setWorkflow(wf);
        return ProcessInstanceRequest.builder().processInstances(Collections.singletonList(wf)).requestInfo(request.getRequestInfo()).build();
    }
}
