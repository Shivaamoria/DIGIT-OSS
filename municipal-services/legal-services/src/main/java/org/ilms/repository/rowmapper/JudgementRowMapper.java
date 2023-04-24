package org.ilms.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tracer.model.CustomException;
import org.ilms.web.model.AuditDetails;
import org.ilms.web.model.enums.Status;
import org.ilms.web.model.judgement;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class JudgementRowMapper implements ResultSetExtractor<List<judgement>> {
    @Autowired
    private ObjectMapper mapper;

    private int full_count = 0;

    public int getFull_count() {
        return full_count;
    }

    public void setFull_count(int full_count) {
        this.full_count = full_count;
    }

    public List<judgement> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<String, judgement> judgementMap = new LinkedHashMap<String, judgement>();
        this.setFull_count(0);
        while (rs.next()) {
            judgement currentjudgement = null;
            String id = rs.getString("id");
            String tenantId = rs.getString("tenant_id");
            String orderType = rs.getString("order_type");
            Long orderDate = rs.getLong("order_date");
            String decisionStatus = rs.getString("decision_status");
            Long complianceDate = rs.getLong("compliance_date");
            Long revisedComplianceDate = rs.getLong("revised_compliance_date");
            String orderNoOverride = rs.getString("order_no_override");
            String cadeId = rs.getString("case_id");
            String revisedComplainceReason = rs.getString("revised_complaince_reason");
            String complianceStatus = rs.getString("compliance_status");
            String remarks = rs.getString("remarks");
            Object additionalDetails = getAdditionalDetail("additional_details", rs);
            String status = rs.getString("status");
            AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("createdby")).createdTime(rs.getLong("createdtime"))
                    .lastModifiedBy(rs.getString("lastmodifiedby"))
                    .lastModifiedTime(rs.getLong("lastmodifiedtime")).build();
            this.setFull_count(rs.getInt("full_count"));
            if (currentjudgement == null) {
                currentjudgement = judgement.builder().id(id).orderType(orderType).orderDate(orderDate).decisionStatus(decisionStatus).tenantId(tenantId)
                        .complianceDate(complianceDate).revisedComplianceDate(revisedComplianceDate)
                        .orderNoOverride(orderNoOverride).caseId(cadeId).revisedComplainceReason(revisedComplainceReason)
                        .complianceStatus(complianceStatus).remarks(remarks).additionalDetails(additionalDetails)
                        .status(Status.valueOf(status)).auditDetails(auditDetails).build();
            }
            judgementMap.put(id, currentjudgement);
        }
        return new ArrayList<>(judgementMap.values());
    }

    private JsonNode getAdditionalDetail(String columnName, ResultSet rs) {

        JsonNode additionalDetail = null;
        try {
            PGobject pgObj = (PGobject) rs.getObject(columnName);
            if (pgObj != null) {
                additionalDetail = mapper.readTree(pgObj.getValue());
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            throw new CustomException("PARSING_ERROR", "Failed to parse additionalDetail object");
        }
        return additionalDetail;
    }
}