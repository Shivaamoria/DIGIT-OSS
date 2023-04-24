package org.ilms.util;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.producer.Producer;
import org.ilms.repository.ServiceRepository;
import org.ilms.web.model.Case;
import org.ilms.web.model.Recepient;
import org.ilms.web.model.enums.Source;
import org.ilms.web.model.notification.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static org.ilms.util.ILMSConstants.*;

@Slf4j
@Component
public class NotificationUtil {
    @Autowired
    ILMSConfiguration ilmsConfiguration;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Producer producer;

    @Autowired
    ServiceRepository serviceRepository;

    public List<String> fetchChannelList(RequestInfo requestInfo, String tenantId, String moduleName, String action) {
        List<String> masterData = new ArrayList<>();
        StringBuilder uri = new StringBuilder();
        uri.append(ilmsConfiguration.getMdmsHost()).append(ilmsConfiguration.getMdmsEndpoint());
        if (StringUtils.isEmpty(tenantId)) {
            return masterData;
        }
        MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestForChannelList(requestInfo, tenantId.split("\\.")[0]);
        Filter masterDataFilter = filter(where(MODULE).is(moduleName).and(ACTION).is(action));

        try {
            Object response = restTemplate.postForObject(uri.toString(), mdmsCriteriaReq, Map.class);
            masterData = JsonPath.parse(response).read("$.MdmsRes.Channel.channelList[?].channelNames[*]", masterDataFilter);
        } catch (Exception e) {
            log.error("Exception while fetching workflow states to ignore: ", e);
        }

        return masterData;
    }

    private MdmsCriteriaReq getMdmsRequestForChannelList(RequestInfo requestInfo, String tenantId) {
        MasterDetail masterDetail = new MasterDetail();
        masterDetail.setName(CHANNEL_LIST);
        List<MasterDetail> masterDetailList = new ArrayList<>();
        masterDetailList.add(masterDetail);

        ModuleDetail moduleDetail = new ModuleDetail();
        moduleDetail.setMasterDetails(masterDetailList);
        moduleDetail.setModuleName(CHANNEL);
        List<ModuleDetail> moduleDetailList = new ArrayList<>();
        moduleDetailList.add(moduleDetail);

        MdmsCriteria mdmsCriteria = new MdmsCriteria();
        mdmsCriteria.setTenantId(tenantId);
        mdmsCriteria.setModuleDetails(moduleDetailList);

        MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
        mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
        mdmsCriteriaReq.setRequestInfo(requestInfo);

        return mdmsCriteriaReq;
    }

    public List<SMSRequest> createSMSRequest(String message, Map<String, String> mobileNumberOfUser) {

        List<SMSRequest> smsRequest = new LinkedList<>();
        for (Map.Entry<String, String> entryset : mobileNumberOfUser.entrySet()) {
            smsRequest.add(new SMSRequest(entryset.getValue(), message));
        }
        return smsRequest;
    }

    public void sendSMS(List<SMSRequest> smsRequestList) {
        if (ilmsConfiguration.getIsSMSNotificationEnabled()) {
            if (CollectionUtils.isEmpty(smsRequestList)) {
                log.info("Messages from localization couldn't be fetched!");
            }
            for (SMSRequest smsRequest : smsRequestList) {
                producer.push(ilmsConfiguration.getSmsNotifTopic(), smsRequest);
                log.info("Sending SMS notification: ");
                log.info("MobileNumber: " + smsRequest.getMobileNumber() + " Messages: " + smsRequest.getMessage());
            }
        }
    }

    public String getLocalizationMessages(String tenantId, RequestInfo requestInfo) {

        String locale = NOTIFICATION_LOCALE;
        Boolean isRetryNeeded = false;
        String jsonString = null;
        LinkedHashMap responseMap = null;

        if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("\\|").length >= 2) {
            locale = requestInfo.getMsgId().split("\\|")[1];
            isRetryNeeded = true;
        }

        responseMap = (LinkedHashMap) serviceRepository.fetchResult(getUri(tenantId, requestInfo, locale), requestInfo).get();
        jsonString = new JSONObject(responseMap).toString();

        if (StringUtils.isEmpty(jsonString) && isRetryNeeded) {

            responseMap = (LinkedHashMap) serviceRepository.fetchResult(getUri(tenantId, requestInfo, NOTIFICATION_LOCALE), requestInfo).get();
            jsonString = new JSONObject(responseMap).toString();
            if (StringUtils.isEmpty(jsonString)) {
                throw new CustomException("LOCALE_ERROR", "Localisation values not found for notifications");
            }
        }
        return jsonString;
    }

    public StringBuilder getUri(String tenantId, RequestInfo requestInfo, String locale) {

        if (ilmsConfiguration.getIsLocalizationStateLevel()) {
            tenantId = tenantId.split("\\.")[0];
        }
        StringBuilder uri = new StringBuilder();
        uri.append(ilmsConfiguration.getLocalizationHost()).append(ilmsConfiguration.getLocalizationContextPath())
                .append(ilmsConfiguration.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale).append("&tenantId=")
                .append(tenantId).append("&module=").append(NOTIFICATION_MODULENAME);

        return uri;
    }

    public String getMessageTemplate(String notificationCode, String localizationMessage) {

        String path = "$..messages[?(@.code==\"{}\")].message";
        path = path.replace("{}", notificationCode);
        String message = "";
        try {
            Object messageObj = JsonPath.parse(localizationMessage).read(path);
            message = ((ArrayList<String>) messageObj).get(0);
        } catch (Exception e) {
            log.warn("Fetching from localization failed", e);
        }
        return message;
    }

    public String getShortenedUrl(String url) {

        HashMap<String, String> body = new HashMap<>();
        body.put("url", url);
        String res = restTemplate.postForObject(ilmsConfiguration.getUrlShortnerHost() + ilmsConfiguration.getUrlShortnerEndpoint(), body, String.class);

        if (StringUtils.isEmpty(res)) {
            log.error("URL_SHORTENING_ERROR", "Unable to shorten url: " + url);
            return url;
        } else {
            return res;
        }
    }

    public void sendEmail(List<EmailRequest> emailRequestList) {

        if (ilmsConfiguration.getIsEmailNotificationEnabled()) {
            if (CollectionUtils.isEmpty(emailRequestList))
                log.info("Messages from localization couldn't be fetched!");
            for (EmailRequest emailRequest : emailRequestList) {
                if (!StringUtils.isEmpty(emailRequest.getEmail().getBody())) {
                    producer.push(ilmsConfiguration.getEmailNotifTopic(), emailRequest);
                    log.info("Sending EMAIL notification! ");
                    log.info("Email Id: " + emailRequest.getEmail().toString());
                } else {
                    log.info("Email body is empty, hence no email notification will be sent.");
                }
            }

        }
    }

    public List<EmailRequest> createEmailRequestFromSMSRequests(RequestInfo requestInfo, List<SMSRequest> smsRequests, String tenantId) {
        Set<String> mobileNumbers = smsRequests.stream().map(SMSRequest::getMobileNumber).collect(Collectors.toSet());
        Map<String, String> mobileNumberToEmailId = fetchUserEmailIds(mobileNumbers, tenantId);
        if (CollectionUtils.isEmpty(mobileNumberToEmailId.keySet())) {
            log.error("Email Ids Not found for Mobilenumbers");
        }

        Map<String, String> mobileNumberToMsg = smsRequests.stream().collect(Collectors.toMap(SMSRequest::getMobileNumber, SMSRequest::getMessage));
        List<EmailRequest> emailRequest = new LinkedList<>();
        for (Map.Entry<String, String> entryset : mobileNumberToEmailId.entrySet()) {
            String message = mobileNumberToMsg.get(entryset.getKey());
            String subject = ilmsConfiguration.getNotifSubject();
            String body = message;
            Email emailobj = Email.builder().emailTo(Collections.singleton(entryset.getValue())).isHTML(false).body(body).subject(subject).build();
            EmailRequest email = new EmailRequest(requestInfo, emailobj);
            emailRequest.add(email);
        }
        return emailRequest;
    }

    public Map<String, String> fetchUserEmailIds(Set<String> mobileNumbers, String tenantId) {
        Map<String, String> mapOfPhnoAndEmailIds = new HashMap<>();
        StringBuilder uri = new StringBuilder();
        uri.append(ilmsConfiguration.getUserHost()).append(ilmsConfiguration.getUserSearchEndPoint());
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put("tenantId", tenantId);
        for (String mobileNo : mobileNumbers) {
            userSearchRequest.put("mobileNumber", mobileNo);
            try {
                Object user = serviceRepository.fetchResult(uri, userSearchRequest).get();
                if (null != user) {
                    if (JsonPath.read(user, "$.user[0].emailId") != null) {
                        String email = JsonPath.read(user, "$.user[0].emailId");
                        mapOfPhnoAndEmailIds.put(mobileNo, email);
                    }
                } else {
                    log.error("Service returned null while fetching user for username - " + mobileNo);
                }
            } catch (Exception e) {
                log.error("Exception while fetching user for username - " + mobileNo);
                log.error("Exception trace: ", e);
                continue;
            }
        }
        return mapOfPhnoAndEmailIds;
    }

    public void sendEventNotification(EventRequest request) {
        log.info("EVENT notification sent!");
        producer.push(ilmsConfiguration.getSaveUserEventsTopic(), request);
    }

    public List<Event> enrichEvent(List<SMSRequest> smsRequests, RequestInfo requestInfo, String tenantId, Case cases) {
        Set<String> mobileNumbers = smsRequests.stream().map(SMSRequest::getMobileNumber).collect(Collectors.toSet());
        Set<String> message = smsRequests.stream().map(SMSRequest::getMessage).collect(Collectors.toSet());
        Map<String, String> mapOfPhnoAndUUIDs = fetchUserUUIDs(mobileNumbers, requestInfo, tenantId);

        if (CollectionUtils.isEmpty(mapOfPhnoAndUUIDs.keySet())) {
            log.error("UUIDs Not found for Mobilenumbers");
        }
        List<Event> events = new ArrayList<>();
        List<String> toUsers = new ArrayList<>();
        if (!(cases.getWorkflow().getAssignes()).isEmpty()) {
            toUsers.add(cases.getWorkflow().getAssignes().get(0).getUuid());
        } else {
            toUsers.add(requestInfo.getUserInfo().getUuid());
        }
        Action action = null;

        Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
        events.add(Event.builder().tenantId(tenantId).description(message.toString()).eventType(USREVENTS_EVENT_TYPE)
                .name(USREVENTS_EVENT_NAME).postedBy(USREVENTS_EVENT_POSTEDBY)
                .source(Source.WEBAPP).recepient(recepient).actions(action).eventDetails(null).build());
        return events;
    }

    public Map<String, String> fetchUserUUIDs(Set<String> mobileNumbers, RequestInfo requestInfo, String tenantId) {

        Map<String, String> mapOfPhnoAndUUIDs = new HashMap<>();
        StringBuilder uri = new StringBuilder();
        uri.append(ilmsConfiguration.getUserHost()).append(ilmsConfiguration.getUserSearchEndPoint());
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put("tenantId", tenantId);
        userSearchRequest.put("userType", "EMPLOYEE");
        for (String mobileNo : mobileNumbers) {
            userSearchRequest.put("mobileNumber", mobileNo);
            try {
                Object user = serviceRepository.fetchResult(uri, userSearchRequest).get();
                if (null != user) {
                    String uuid = JsonPath.read(user, "$.user[0].uuid");
                    mapOfPhnoAndUUIDs.put(mobileNo, uuid);
                } else {
                    log.error("Service returned null while fetching user for username - " + mobileNo);
                }
            } catch (Exception e) {
                log.error("Exception while fetching user for username - " + mobileNo);
                log.error("Exception trace: ", e);
                continue;
            }
        }
        return mapOfPhnoAndUUIDs;
    }

}
