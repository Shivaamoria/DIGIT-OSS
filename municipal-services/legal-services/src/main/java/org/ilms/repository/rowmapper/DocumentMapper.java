package org.ilms.repository.rowmapper;

import org.ilms.web.model.AuditDetails;
import org.ilms.web.model.Document;
import org.ilms.web.model.enums.Status;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DocumentMapper implements ResultSetExtractor<List<Document>> {

    private int fullCount = 0;

    public int getFullCount() {
        return fullCount;
    }

    public void setFullCount(int full_count) {
        this.fullCount = full_count;
    }

    @Override
    public List<Document> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, Document> documentList = new LinkedHashMap<String, Document>();
        this.setFullCount(0);
        while (rs.next()) {
            String id = rs.getString("id");
            if (Status.valueOf(rs.getString("status")) == Status.ACTIVE) {
                this.setFullCount((rs.getInt("document_full_count")));
                AuditDetails auditDetails = AuditDetails.builder()
                        .createdBy(rs.getString("createdby")).createdTime(rs.getLong("createdtime"))
                        .lastModifiedBy(rs.getString("lastmodifiedby")).lastModifiedTime(rs.getLong("lastmodifiedtime"))
                        .build();

                Document currentDocument = Document.builder().id(rs.getString("id")).caseId(rs.getString("case_id"))
                        .documentType(rs.getString("document_type")).status(Status.valueOf(rs.getString("status")))
                        .remarks(rs.getString("remarks")).fileStoreId(rs.getString("file_store_id")).documentId(rs.getString("document_id")).auditDetails(auditDetails).build();

                documentList.put(id, currentDocument);
            }
        }
        return new ArrayList<>(documentList.values());
    }
}