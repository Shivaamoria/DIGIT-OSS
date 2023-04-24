package org.ilms.repository.querybuilder;

import org.apache.commons.lang3.StringUtils;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.web.model.CaseSearchCriteria;
import org.ilms.web.model.CountRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class CaseQueryBuilder {
    private static final String docQuery = "select count(*) OVER() AS document_full_count,* from ilms_document where case_id = ?";

    private static final String partyQuery = "select party.*,advocate.id as adv_id, advocate.first_name, advocate.last_name,advocate.contact_number,advocate.status,advocate.createdby,advocate.createdtime,advocate.lastmodifiedby,advocate.lastmodifiedtime from ilms_case_party party inner join ilms_advocate advocate ON advocate.id = party.advocate_id where party.case_id=?";

    private static final String Query = "select count(*) OVER() AS full_count,ilms_case.id as ilmsCase_id, ilms_case.case_number as ilms_caseNumber, ilms_case.cnr_number as ilms_cnrNumber, ilms_case.tenant_id as ilms_tenantId, ilms_case.parent_case_id as ilms_parentCaseId, ilms_case.linked_cases as ilms_linkedCases, ilms_case.case_type as ilms_caseType, ilms_case.case_category as ilms_caseCategory, ilms_case.filing_number as ilms_filingNumber, ilms_case.filing_date as ilms_filingDate, ilms_case.registrtion_date as ilms_registrationDate, ilms_case.case_summary as ilms_caseSummary, ilms_case.arising_details as ilms_arisingDetails, ilms_case.policy_or_nonpolicy_matter as ilms_matter, ilms_case.case_status as ilms_caseStatus, ilms_case.case_stage as ilms_caseStage, ilms_case.case_sub_stage as ilms_caseSubStage, ilms_case.priority as ilms_priority, ilms_case.recommend_oic as ilms_recommendOic, ilms_case.remarks as ilms_remarks, ilms_case.additional_details as ilms_additionalDetails, ilms_case.status as ilms_status, ilms_case.createdby as ilms_createdBy, ilms_case.createdtime as ilms_createdTime, ilms_case.lastmodifiedby as ilms_lastModifiedBy, ilms_case.lastmodifiedtime as ilms_lastModifiedTime, ilms_act.id as actId, ilms_act.case_id as actCaseId, ilms_act.name as actName,ilms_court.id as court_id, ilms_court.case_id as court_caseId, ilms_court.court_name as court_name, ilms_court.district as court_district, ilms_court.state as court_state, ilms_court.division as court_division,ilms_court.status as court_status, ilms_court.createdby as court_createdby,ilms_court.createdtime as court_createdtime,ilms_court.lastmodifiedby as court_lastmodifiedby,ilms_court.lastmodifiedtime as court_lastmodifiedtime, ilms_act.section_number as actSectionNumber, ilms_act.status as actStatus,  ilms_act.createdby as act_createdBy, ilms_act.createdtime as act_createdTime, ilms_act.lastmodifiedby as act_lastModifiedBy, ilms_act.lastmodifiedtime as act_lastModifiedTime FROM ilms_case LEFT OUTER JOIN ilms_court on ilms_court.case_id = ilms_case.id  LEFT OUTER JOIN ilms_act on ilms_act.case_id = ilms_case.id";

    private static final String ChildCaseQuery = "SELECT id FROM ilms_case where id= ? or parent_case_id= ? ";

    private static final String TOTALCOUNTQUERY = "select count(*) from ilms_case ";

    private static final String CaseQuery1 = "select DISTINCT(cases.id) from ilms_case as cases INNER JOIN eg_wf_processinstance_v2 pi ON pi.businessid = cases.id LEFT JOIN eg_wf_assignee_v2 assg ON pi.id = assg.processinstanceid ";
    private static final String CaseQuery2 = " AND pi.createdtime IN (select max(createdtime) from eg_wf_processinstance_v2 wf where wf.businessid = cases.id GROUP BY wf.businessid)";

    private final String paginationWrapper = "{} {orderBy} {pagination}";

    @Autowired
    private ILMSConfiguration ilmsConfiguration;

    public String getILMSCaseSearchQuery(CaseSearchCriteria criteria, List<Object> preparedStmtList) {

        StringBuilder builder = new StringBuilder(Query);
        if (criteria.getCnrNumber() != null) {
            if (criteria.getCnrNumber().split("\\.").length == 1) {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" ilms_case.cnr_number like ?");
                preparedStmtList.add('%' + criteria.getCnrNumber() + '%');
            } else {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" ilms_case.cnr_number = ?");
                preparedStmtList.add('%' + criteria.getCnrNumber() + '%');
            }
        }

        List<String> caseNumber = criteria.getNumber();
        try {
            if (!CollectionUtils.isEmpty(caseNumber)) {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" ilms_case.case_number IN (").append(createQuery(caseNumber)).append(")");
                addToPreparedStatement(preparedStmtList, caseNumber);
            }
        } catch (NullPointerException e) {
            preparedStmtList.add("");
        }
        List<String> caseId = criteria.getId();
        try {
            if (!CollectionUtils.isEmpty(caseId)) {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" ilms_case.id IN (").append(createQuery(caseId)).append(")");
                addToPreparedStatement(preparedStmtList, caseId);
            }
        } catch (NullPointerException e) {
            preparedStmtList.add("");
        }
        List<String> parentCaseId = criteria.getParentCaseId();
        try {
            if (!CollectionUtils.isEmpty(parentCaseId)) {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" ilms_case.parent_case_id IN (").append(createQuery(parentCaseId)).append(")");
                addToPreparedStatement(preparedStmtList, parentCaseId);
            }
        } catch (NullPointerException e) {
            preparedStmtList.add("");
        }
        return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
    }

    /**
     * @param query            prepared Query
     * @param preparedStmtList values to be replased on the query
     * @param criteria         ilms case search criteria
     * @return the query by replacing the placeholders with preparedStmtList
     */
    private String addPaginationWrapper(String query, List<Object> preparedStmtList, CaseSearchCriteria criteria) {

        int limit = ilmsConfiguration.getDefaultLimit();
        int offset = ilmsConfiguration.getDefaultOffset();
        String finalQuery = paginationWrapper.replace("{}", query);
        if (criteria.getLimit() != null && criteria.getLimit() <= ilmsConfiguration.getMaxSearchLimit()) {
            limit = criteria.getLimit();
        }
        if (criteria.getLimit() != null && criteria.getLimit() > ilmsConfiguration.getMaxSearchLimit()) {
            limit = ilmsConfiguration.getMaxSearchLimit();
        }
        if (criteria.getOffset() != null) {
            offset = criteria.getOffset();
        }
        StringBuilder orderQuery = new StringBuilder();
        addOrderByClause(orderQuery, criteria);
        finalQuery = finalQuery.replace("{orderBy}", orderQuery.toString());
        if (limit == -1) {
            finalQuery = finalQuery.replace("{pagination}", "");
        } else {
            finalQuery = finalQuery.replace("{pagination}", " offset ?  limit ?  ");
            preparedStmtList.add(offset);
            preparedStmtList.add(limit);
        }
        return finalQuery;
    }

    private void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
        if (values.isEmpty()) {
            queryString.append(" WHERE ");
        } else {
            queryString.append(" AND");
        }
    }

    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }

    private Object createQuery(List<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    /**
     *
     */
    private void addOrderByClause(StringBuilder builder, CaseSearchCriteria criteria) {
        if (criteria.getSortBy() == CaseSearchCriteria.SortBy.caseNumber) {
            builder.append(" ORDER BY ilms_case.case_number ");
        } else if (criteria.getSortBy() == CaseSearchCriteria.SortBy.cnrNumber) {
            builder.append(" ORDER BY ilms_case.cnr_number ");
        }
        if (criteria.getSortOrder() == CaseSearchCriteria.SortOrder.ASC) {
            builder.append("ASC");
        } else if (criteria.getSortOrder() == CaseSearchCriteria.SortOrder.DESC) {
            builder.append("DESC");
        }
    }

    public String getDocQuery() {
        return docQuery;
    }

    public String getPartyQuery() {
        return partyQuery;
    }

    public String getChildCaseIds(String parentCaseId, List<Object> preparedStmtList) {
        return ChildCaseQuery;
    }

    public CountRequest getTotalCount(CaseSearchCriteria criteria) {
        CountRequest finalRequest = new CountRequest();
        List<Object> preparedStmtList = new ArrayList<>();
        StringBuilder builder = new StringBuilder(TOTALCOUNTQUERY);
        //        if (!CollectionUtils.isEmpty(Collections.singleton(criteria.getCnrNumber()))) {
        if (StringUtils.isNotBlank(criteria.getCnrNumber())) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append("cnr_number= ?");
            preparedStmtList.add(criteria.getCnrNumber());
        } else if (Objects.nonNull(criteria.getNumber())) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append("case_number= ?");
            preparedStmtList.add(criteria.getNumber().get(0));
        }
        addClauseIfRequired(preparedStmtList, builder);
        builder.append(" status = 'ACTIVE' ");
        finalRequest.setQuery(builder.toString());
        finalRequest.setPreparedStatement(preparedStmtList);
        return finalRequest;
    }


    public String getAssignedCases(String uuid) {
        return CaseQuery1 + "where " + "assg.assignee = " + "'" + uuid + "'" + CaseQuery2;
    }
}
