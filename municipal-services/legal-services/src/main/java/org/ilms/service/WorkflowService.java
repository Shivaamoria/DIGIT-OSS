package org.ilms.service;

import java.util.*;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.repository.ServiceRepository;
import org.ilms.util.CaseUtils;
import org.ilms.util.CommonUtils;
import org.ilms.util.ILMSConstants;
import org.ilms.web.model.Case;
import org.ilms.web.model.CaseRequest;
import org.ilms.web.model.RequestInfoWrapper;
import org.ilms.web.model.enums.CreationReason;
import org.ilms.web.model.workflow.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Service
public class WorkflowService {
    @Autowired
    private ILMSConfiguration ilmsConfiguration;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CaseUtils caseUtils;

    /**
     * Method to integrate with workflow
     * <p>
     * takes the trade-license request as parameter constructs the work-flow request
     * <p>
     * and sets the resultant status from wf-response back to trade-license object
     */
    public State callWorkFlow(ProcessInstanceRequest workflowReq) {

        ProcessInstanceResponse response = null;
        StringBuilder url = new StringBuilder(ilmsConfiguration.getWfHost().concat(ilmsConfiguration.getWfTransitionPath()));
        Optional<Object> optional = serviceRepository.fetchResult(url, workflowReq);
        response = mapper.convertValue(optional.get(), ProcessInstanceResponse.class);
        return response.getProcessInstances().get(0).getState();
    }

    /**
     * Get the workflow config for the given tenant
     *
     * @param tenantId The tenantId for which businessService is requested
     * @param requestInfo The RequestInfo object of the request
     * @return BusinessService for the the given tenantId
     */
    public BusinessService getBusinessService(String tenantId, String businessService, RequestInfo requestInfo) {

        StringBuilder url = getSearchURLWithParams(tenantId, businessService);
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        Optional<Object> result = serviceRepository.fetchResult(url, requestInfoWrapper);
        BusinessServiceResponse response = null;
        try {
            response = mapper.convertValue(result.get(), BusinessServiceResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException("PARSING ERROR", "Failed to parse response of workflow business service search");
        }

        if (CollectionUtils.isEmpty(response.getBusinessServices())) {
            throw new CustomException("BUSINESSSERVICE_NOT_FOUND", "The businessService " + businessService + " is not found");
        }

        return response.getBusinessServices().get(0);
    }

    /**
     * Creates url for search based on given tenantId
     *
     * @param tenantId The tenantId for which url is generated
     * @return The search url
     */
    private StringBuilder getSearchURLWithParams(String tenantId, String businessService) {

        StringBuilder url = new StringBuilder(ilmsConfiguration.getWfHost());
        url.append(ilmsConfiguration.getWfBusinessServiceSearchPath());
        url.append("?tenantId=");
        url.append(tenantId);
        url.append("&businessServices=");
        url.append(businessService);
        return url;
    }

    /**
     * method to prepare process instance request
     * and assign status back to property
     */
    public State updateWorkflow(CaseRequest request, CreationReason creationReasonForWorkflow) {

        Case cases = request.getCaseObj();

        ProcessInstanceRequest workflowReq = caseUtils.getWfForCaseCreate(request, creationReasonForWorkflow);
        State state = callWorkFlow(workflowReq);

        if (state.getApplicationStatus().equalsIgnoreCase(ilmsConfiguration.getWfStatusActive()) && cases.getId() == null) {

            String pId = commonUtils.getIdList(request.getRequestInfo(), cases.getTenantId(), ilmsConfiguration.getCaseIdgenName(),
                    ilmsConfiguration.getCaseIdgenFormat(), 1).get(0);
            request.getCaseObj().setId(pId);
        }

        request.getCaseObj().getWorkflow().setState(state);
        return state;
    }

    /**
     * Returns boolean value to specifying if the state is updatable
     *
     * @param stateCode The stateCode of the license
     * @param businessService The BusinessService of the application flow
     * @return State object to be fetched
     */
    public Boolean isStateUpdatable(String stateCode, BusinessService businessService) {
        for (State state : businessService.getStates()) {
            if (state.getState() != null && state.getState().equalsIgnoreCase(stateCode)) {
                return state.getIsStateUpdatable();
            }
        }
        return null;
    }

    /**
     * Creates url for searching processInstance
     *
     * @return The search url
     */
    private StringBuilder getWorkflowSearchURLWithParams(String tenantId, String businessId) {

        StringBuilder url = new StringBuilder(ilmsConfiguration.getWfHost());
        url.append(ilmsConfiguration.getWfProcessInstanceSearchPath());
        url.append("?tenantId=");
        url.append(tenantId);
        url.append("&businessIds=");
        url.append(businessId);
        return url;
    }

    /**
     * Fetches the workflow object for the given assessment
     */
    public ProcessInstanceResponse getWorkflow(RequestInfo requestInfo, String tenantId, String businessId) {

        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

        StringBuilder url = getWorkflowSearchURLWithParams(tenantId, businessId);

        Optional<Object> res = serviceRepository.fetchResult(url, requestInfoWrapper);
        ProcessInstanceResponse response = null;

        try {
            response = mapper.convertValue(res.get(), ProcessInstanceResponse.class);
        } catch (Exception e) {
            throw new CustomException("PARSING_ERROR", "Failed to parse workflow search response");
        }

        if (response != null && !CollectionUtils.isEmpty(response.getProcessInstances()) && response.getProcessInstances().get(0) != null) {
            return response;
        }

        return null;
    }

    public List<HashMap<String, Object>> getProcessStatusCount(RequestInfo requestInfo,
                                                               ProcessInstanceSearchCriteria criteria) {
        List<String> listOfBusinessServices = new ArrayList<>(criteria.getBusinessService());
        List<HashMap<String, Object>> finalResponse = null;
        for (String businessSrv : listOfBusinessServices) {
            criteria.setBusinessService(Collections.singletonList(businessSrv));
            StringBuilder url = new StringBuilder(ilmsConfiguration.getWfHost());
            url.append(ilmsConfiguration.getProcessStatusCountPath());
            criteria.setIsProcessCountCall(true);
            // For BPA having large request, so that it was sending from the body
            List<String> roles = requestInfo.getUserInfo().getRoles().stream().map(Role::getCode).collect(Collectors.toList());
            if (!ObjectUtils.isEmpty(criteria.getModuleName()))
                url = this.buildWorkflowUrl(criteria, url, Boolean.FALSE);
            RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
            if (finalResponse == null) {
                finalResponse = (List<HashMap<String, Object>>) serviceRepository.fetchListResult(url,
                        requestInfoWrapper);
            }
        }
        criteria.setBusinessService(listOfBusinessServices);
        return finalResponse;
    }

    private StringBuilder buildWorkflowUrl(ProcessInstanceSearchCriteria criteria, StringBuilder url, boolean noStatus) {
        url.append("?tenantId=").append(criteria.getTenantId());
        if(!CollectionUtils.isEmpty(criteria.getStatus()) && noStatus == Boolean.FALSE) {
            url.append("&status=").append(StringUtils.arrayToDelimitedString(criteria.getStatus().toArray(),","));
        }

        if(!CollectionUtils.isEmpty(criteria.getBusinessIds())) {
            url.append("&businessIds=").append(StringUtils.arrayToDelimitedString(criteria.getBusinessIds().toArray(),","));
        }

        if(!CollectionUtils.isEmpty(criteria.getIds())) {
            url.append("&ids=").append(StringUtils.arrayToDelimitedString(criteria.getIds().toArray(),","));
        }
        if(!StringUtils.isEmpty(criteria.getAssignee())) {
            url.append("&assignee=").append( criteria.getAssignee());
        }
        if(criteria.getHistory() != null) {
            url.append("&history=").append( criteria.getHistory());
        }
        if(criteria.getFromDate() != null) {
            url.append("&fromDate=").append( criteria.getFromDate());
        }
        if(criteria.getToDate() != null) {
            url.append("&toDate=").append( criteria.getToDate());
        }

        if(!StringUtils.isEmpty(criteria.getModuleName())) {
            url.append("&moduleName=").append( criteria.getModuleName());
        }
        if(criteria.getIsProcessCountCall() || ObjectUtils.isEmpty(criteria.getModuleName()) && !StringUtils.isEmpty(criteria.getBusinessService())) {
            url.append("&businessService=").append( StringUtils.arrayToDelimitedString(criteria.getBusinessService().toArray(),","));
        }
        if(!StringUtils.isEmpty(criteria.getLimit())) {
            url.append("&limit=").append( criteria.getLimit());
        }
        if(!StringUtils.isEmpty(criteria.getOffset())) {
            url.append("&offset=").append( criteria.getOffset());
        }

        return url;
    }


}