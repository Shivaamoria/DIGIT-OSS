package org.ilms.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.repository.ServiceRepository;
import org.ilms.web.model.idGen.IdGenerationRequest;
import org.ilms.web.model.idGen.IdGenerationResponse;
import org.ilms.web.model.idGen.IdRequest;
import org.ilms.web.model.idGen.IdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@Component
public class CommonUtils {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ILMSConfiguration configs;

    @Autowired
    private ServiceRepository restRepo;
    
    public Map<String, List<String>> getAttributeValues(String tenantId, String moduleName, List<String> names, String filter, String jsonpath,
            RequestInfo requestInfo) {

        StringBuilder uri = new StringBuilder(configs.getMdmsHost()).append(configs.getMdmsEndpoint());
        MdmsCriteriaReq criteriaReq = prepareMdMsRequest(tenantId, moduleName, names, filter, requestInfo);
        Optional<Object> response = restRepo.fetchResult(uri, criteriaReq);

        try {
            if (response.isPresent()) {
                return JsonPath.read(response.get(), jsonpath);
            }
        } catch (Exception e) {
            throw new CustomException(ILMSErrorConstants.INVALID_TENANT_ID_MDMS_KEY, ILMSErrorConstants.INVALID_TENANT_ID_MDMS_MSG);
        }

        return null;
    }

    public MdmsCriteriaReq prepareMdMsRequest(String tenantId, String moduleName, List<String> names, String filter, RequestInfo requestInfo) {

        List<MasterDetail> masterDetails = new ArrayList<>();

        names.forEach(name -> {
            masterDetails.add(MasterDetail.builder().name(name).filter(filter).build());
        });

        ModuleDetail moduleDetail = ModuleDetail.builder().moduleName(moduleName).masterDetails(masterDetails).build();
        List<ModuleDetail> moduleDetails = new ArrayList<>();
        moduleDetails.add(moduleDetail);
        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(tenantId).moduleDetails(moduleDetails).build();
        return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
    }

    public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, int count) {

        List<IdRequest> reqList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            reqList.add(IdRequest.builder().idName(idName).format(idformat).tenantId(tenantId).build());
        }

        IdGenerationRequest request = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo).build();
        StringBuilder uri = new StringBuilder(configs.getIdGenHost()).append(configs.getIdGenPath());
        IdGenerationResponse response = mapper.convertValue(restRepo.fetchResult(uri, request).get(), IdGenerationResponse.class);

        List<IdResponse> idResponses = response.getIdResponses();

        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");
        }

        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }

    public Map<String, String> fetchUsersByUUID(List<String> listUuids, String tenantId) {
        StringBuilder uri = new StringBuilder();
        uri.append(configs.getUserHost()).append(configs.getUserSearchEndPoint());
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put("tenantId", tenantId);
        userSearchRequest.put("uuid", listUuids);
        Map<String, String> roleList = new HashMap<>();
        try {
            Object user = restRepo.fetchUserResult(uri, userSearchRequest);
            if (user != null) {
                String role = JsonPath.read(user, "$.user[0].roles[0].code");
                roleList.put("role", role);
            }
        } catch (Exception e) {
            throw new CustomException(ILMSErrorConstants.UNABLE_TO_FETCH, "Unable to fetch User from system");
        }
        return roleList;
    }

    public boolean isUserExists(List<String> listUuids, String tenantId) {
        Map<String, String> userRoles = fetchUsersByUUID(listUuids, tenantId);
        if (userRoles.get("role").isEmpty()) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                     "User does not exists in system");
        }
        return true;
    }
public boolean isUserDEC(List<String> listUuids, String tenantId, String columnValue) {
        Map<String, String> userRoles = fetchUsersByUUID(listUuids, tenantId);
        if (!userRoles.get("role").equalsIgnoreCase("DEC")) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "Unauthorised User [ " + userRoles.get("role") + " ] for [ " + columnValue + " ]");
        }
        return true;
    }

    public boolean isUserRO(List<String> listUuids, String tenantId, String columnValue) {
        Map<String, String> userRoles = fetchUsersByUUID(listUuids, tenantId);
        if (!userRoles.get("role").equalsIgnoreCase("RO")) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "Unauthorised User [ " + userRoles.get("role") + " ] for [ " + columnValue + " ]");
        }
        return true;
    }

    public boolean isUserOICA(List<String> listUuids, String tenantId, String columnValue) {
        Map<String, String> userRoles = fetchUsersByUUID(listUuids, tenantId);
        if (!userRoles.get("role").equalsIgnoreCase("OICA")) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "Unauthorised User [ " + userRoles.get("role") + " ] for [ " + columnValue + " ]");
        }
        return true;
    }

    public boolean isUserAO(List<String> listUuids, String tenantId, String columnValue) {
        Map<String, String> userRoles = fetchUsersByUUID(listUuids, tenantId);
        if (!userRoles.get("role").equalsIgnoreCase("AO")) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "Unauthorised User [ " + userRoles.get("role") + " ] for [ " + columnValue + " ]");
        }
        return true;
    }

    public boolean isUserOIC(List<String> listUuids, String tenantId, String columnValue) {
        Map<String, String> userRoles = fetchUsersByUUID(listUuids, tenantId);
        if (!userRoles.get("role").equalsIgnoreCase("OIC")) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "Unauthorised User [ " + userRoles.get("role") + " ] for [ " + columnValue + " ]");
        }
        return true;
    }

    public boolean isUserMO(List<String> listUuids, String tenantId, String columnValue) {
        Map<String, String> userRoles = fetchUsersByUUID(listUuids, tenantId);
        if (!userRoles.get("role").equalsIgnoreCase("MO")) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "Unauthorised User [ " + userRoles.get("role") + " ] for [ " + columnValue + " ]");
        }
        return true;
    }
}
