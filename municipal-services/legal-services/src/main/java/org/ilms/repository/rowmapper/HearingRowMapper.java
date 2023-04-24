package org.ilms.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tracer.model.CustomException;
import org.ilms.web.model.AuditDetails;
import org.ilms.web.model.Hearing;
import org.ilms.web.model.Party;
import org.ilms.web.model.Payment;
import org.ilms.web.model.enums.Status;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HearingRowMapper implements ResultSetExtractor<List<Hearing>> {
    private final Party petitioner = new Party();

    private final Party respondent = new Party();

    @Autowired
    private ObjectMapper mapper;

    private int fullCount = 0;

    public int getFullCount() {
        return fullCount;
    }

    public void setFullCount(int full_count) {
        this.fullCount = full_count;
    }

    @Override
    public List<Hearing> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<String, Hearing> ilmsHearingMap = new LinkedHashMap<String, Hearing>();
        this.setFullCount(0);
        while (rs.next()) {
            String duplicacyCheck = "";
            Hearing currentHearing = new Hearing();

            AuditDetails auditDetails = AuditDetails.builder().createdTime(rs.getLong("hearing_createdtime"))
                    .createdBy(rs.getString("hearing_createdby"))
                    .lastModifiedBy(rs.getString("hearing_lastmodifiedby"))
                    .lastModifiedTime(rs.getLong("hearing_lastmodifiedtime")).build();

            // TODO fill the ILMSCase object with data in the result set record
            if (!duplicacyCheck.equals(rs.getString("hearing_id"))) {
                String id = rs.getString("hearing_id");
                String tenantId = rs.getString("tenant_id");
                duplicacyCheck = id;
                String hearingNumber = rs.getString("hearing_number");

                currentHearing = ilmsHearingMap.get(id);
                String caseId = rs.getString("hearing_case_id");
                currentHearing = ilmsHearingMap.get(id);
                String courtNumber = rs.getString("hearing_court_number");
                String bench = rs.getString("hearing_bench");
                JsonNode judgeName = getJudgeNames("hearing_judge_name", rs);
                Long hearingDate = rs.getLong("hearing_date");
                Long businessDate = rs.getLong("hearing_business_date");
                String hearingPurpose = rs.getString("hearing_purpose");
                String requiredOfficer = rs.getString("hearing_required_officer");
                Long affidavitFilingDate = rs.getLong("affidavit_filing_date");
                Long affidavitFilingDueDate = rs.getLong("affidavit_filing_due_date");
                String caseNumber = rs.getString("hearing_case_number");
                String oathNumber = rs.getString("hearing_oath_number");
                Long nextHearingDate = rs.getLong("next_hearing_date");
                Boolean isPresenceRequired = rs.getBoolean("hearing_is_presence_required");
                String hearingType = rs.getString("hearing_type");
                String departmentOfficer = rs.getString("hearing_department_officer");
                String remarks = rs.getString("hearing_remarks");
                String status = rs.getString("hearing_status");
                Object additionalDetails = getAdditionalDetail("hearing_additionalDetails", rs);
                this.setFullCount((rs.getInt("full_count")));
                if (currentHearing == null) {
                    currentHearing = Hearing.builder().id(id).hearingNumber(hearingNumber).additionalDetails(additionalDetails).caseId(caseId)
                            .judgeName(judgeName).hearingDate(hearingDate).courtNumber(courtNumber)
                            .nextHearingDate(nextHearingDate).bench(bench).tenantId(tenantId)
                            .isPresenceRequired(isPresenceRequired).hearingType(hearingType).departmentOfficer(departmentOfficer)
                            .remarks(remarks).status(Status.valueOf(status)).businessDate(businessDate).hearingPurpose(hearingPurpose)
                            .requiredOfficer(requiredOfficer).auditDetails(auditDetails).affidavitFilingDate(affidavitFilingDate)
                            .affidavitFilingDueDate(affidavitFilingDueDate).caseNumber(caseNumber).oathNumber(oathNumber).build();

                    ilmsHearingMap.put(id, currentHearing);
                }
            }
            addChildrenToHearingDetails(rs, currentHearing);
        }
        return new ArrayList<>(ilmsHearingMap.values());
    }

    @SuppressWarnings("unused")
    private void addChildrenToHearingDetails(ResultSet rs, Hearing hearing) throws SQLException {
        // TODO add all the child data petitioner, respondant, court, advocate

        if (Status.valueOf(rs.getString("payment_status")) == Status.ACTIVE) {
            AuditDetails auditDetails = AuditDetails.builder().createdTime(rs.getLong("payment_createdtime"))
                    .createdBy(rs.getString("payment_createdby"))
                    .lastModifiedBy(rs.getString("payment_lastmodifiedby"))
                    .lastModifiedTime(rs.getLong("payment_lastmodifiedtime")).build();

            Payment payment = Payment.builder().id(rs.getString("payment_id")).caseId(rs.getString("payment_case_id"))
                    .hearingId(rs.getString("payment_hearing_id")).fineImposedDate(rs.getLong("payment_fine_imposed_date"))
                    .fineDueDate(rs.getLong("payment_fine_due_date")).fineAmount(rs.getString("payment_fine_amount"))
                    .status(Status.valueOf(rs.getString("payment_status"))).auditDetails(auditDetails).build();
            hearing.setPayment(payment);
        }
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

    private JsonNode getJudgeNames(String columnName, ResultSet rs) {

        JsonNode judgeNames = null;
        try {
            PGobject pgObj = (PGobject) rs.getObject(columnName);
            if (pgObj != null) {
                judgeNames = mapper.readTree(pgObj.getValue());
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            throw new CustomException("PARSING_ERROR", "Failed to parse Judge Names");
        }
        return judgeNames;
    }
}

