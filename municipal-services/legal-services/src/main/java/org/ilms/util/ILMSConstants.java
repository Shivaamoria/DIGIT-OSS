package org.ilms.util;

import org.springframework.stereotype.Component;

@Component
public class ILMSConstants {
    public static final String Respondent = "Respondent";

    public static final String MDMS_ILMS_MOD_NAME = "common-masters";

    public static final String JSONPATH_CODES = "$.MdmsRes.common-masters";

    public static final String MDMS_ILMS_CASE_TYPE = "CaseType";

    public static final String MDMS_ILMS_CASE_STATUS = "CaseStatus";

    public static final String MDMS_ILMS_CASE_CATEGORY = "CaseCategory";

    public static final String MDMS_ILMS_CASE_STAGE = "CaseStage";

    public static final String MDMS_ILMS_SUB_STAGE = "SubStage";

    public static final String MDMS_ILMS_HEARING_TYPE = "HearingType";

    public static final String MDMS_ILMS_GENDER_TYPE = "GenderType";

    public static final String MDMS_ILMS_PETITIONER_TYPE = "PetitionerType";

    public static final String MDMS_ILMS_DEPARTMENT_NAME = "Department";

    public static final String MDMS_ILMS_DEPARTMENT_IOC = "DepartmentIOC";

    public static final String CASE_FLAG = "CaseFlag";

    public static final String MDMS_ILMS_STATUS_OF_COMPLIANCE = "StageCompliance";

    public static final String MDMS_ILMS_ORDER_TYPE = "OrderType";

    public static final String MDMS_ILMS_DOCUMENT_CATEGORY = "DocumentsCategory";

    public static final String MDMS_ILMS_DISTRICT = "District";

    public static final String MDMS_ILMS_COURT_NAME = "CourtName";

    public static final String MDMS_ILMS_STATE = "State";

    public static final String MDMS_ILMS_BENCH = "Bench";

    public static final String MDMS_ILMS_DIVISION = "Division";

    //Notification Enhancement
    public static final String CHANNEL_NAME_SMS = "SMS";

    public static final String CHANNEL_NAME_EVENT = "EVENT";

    public static final String CHANNEL_NAME_EMAIL = "EMAIL";

    public static final String MODULE = "module";
    public static final String ILMS = "iLMS-services";

    public static final String ACTION = "action";

    public static final String CHANNEL_LIST = "channelList";

    public static final String CHANNEL = "Channel";

    public static final String LOCALIZATION_CODES_JSONPATH = "$.messages.*.code";

    public static final String LOCALIZATION_MSGS_JSONPATH = "$.messages.*.message";

    //  NOTIFICATION PLACEHOLDER

    public static final String NOTIFICATION_USER_NAME = "{name}";

    public static final String NOTIFICATION_EMAIL = "{email_id}";


    public static final String CREATE_STRING = "Create";

    public static final String PT_BUSINESSSERVICE = "iLMS-services";

    public static final String ACTION_FOR_ASSESSMENT = "CREATE";

    public static final String NOTIFICATION_CASEID = "{id}";

    public static final String NOTIFICATION_LOCALE = "en_IN";

    public static final String NOTIFICATION_MODULENAME = "rainmaker-common";

    public static final String UPDATE_STRING = "UPDATE";

    public static final String USREVENTS_EVENT_TYPE = "SYSTEMGENERATED";

    public static final String USREVENTS_EVENT_NAME = "iLMS";

    public static final String USREVENTS_EVENT_POSTEDBY = "SYSTEM-iLMS";

}
