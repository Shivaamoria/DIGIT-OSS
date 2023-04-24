package org.ilms.repository.querybuilder;

import org.ilms.configs.ILMSConfiguration;
import org.ilms.web.model.DocumentSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class DocumentQueryBuilder {
    private static final String Query = "select count(*) OVER() AS document_full_count, id, case_id, document_type, file_store_id, status, remarks,document_id, createdby, createdtime, lastmodifiedby, lastmodifiedtime from ilms_document ";

    private final String paginationWrapper = "{} {orderBy} {pagination}";

    @Autowired
    private ILMSConfiguration ilmsConfiguration;

    public String getDocumentSearchQuery(DocumentSearchCriteria criteria, List<Object> preparedStmtList) {

        StringBuilder builder = new StringBuilder(Query);
        if (criteria.getDocumentType() != null) {
            if (criteria.getDocumentType().split("\\.").length == 1) {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" ilms_document.document_type like ?");
                preparedStmtList.add('%' + criteria.getDocumentType() + '%');
            } else {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" ilms_document.document_type = ?");
                preparedStmtList.add('%' + criteria.getDocumentType() + '%');
            }
        }
        List<String> caseId = criteria.getCaseId();
        try {
            if (!CollectionUtils.isEmpty(caseId)) {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" ilms_document.case_id IN (").append(createQuery(caseId)).append(")");
                addToPreparedStatement(preparedStmtList, caseId);
            }
        } catch (NullPointerException e) {
            preparedStmtList.add("");
        }
        List<String> fileStoreId = criteria.getFileStoreId();
        try {
            if (!CollectionUtils.isEmpty(fileStoreId)) {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" ilms_document.file_store_id IN (").append(createQuery(fileStoreId)).append(")");
                addToPreparedStatement(preparedStmtList, fileStoreId);
            }
        } catch (NullPointerException e) {
            preparedStmtList.add("");
        }
        List<String> id = criteria.getId();
        try {
            if (!CollectionUtils.isEmpty(id)) {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" ilms_document.id IN (").append(createQuery(id)).append(")");
                addToPreparedStatement(preparedStmtList, id);
            }
        } catch (NullPointerException e) {
            preparedStmtList.add("");
        }
        return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
    }

    /**
     * @param query            prepared Query
     * @param preparedStmtList values to be replased on the query
     * @param criteria         document search criteria
     * @return the query by replacing the placeholders with preparedStmtList
     */
    private String addPaginationWrapper(String query, List<Object> preparedStmtList, DocumentSearchCriteria criteria) {
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
    private void addOrderByClause(StringBuilder builder, DocumentSearchCriteria criteria) {
        if (criteria.getSortBy() == DocumentSearchCriteria.SortBy.caseId) {
            builder.append(" ORDER BY ilms_document.case_id ");
        } else if (criteria.getSortBy() == DocumentSearchCriteria.SortBy.documentType) {
            builder.append(" ORDER BY ilms_case.document_type ");
        }
        if (criteria.getSortOrder() == DocumentSearchCriteria.SortOrder.ASC) {
            builder.append("ASC");
        } else if (criteria.getSortOrder() == DocumentSearchCriteria.SortOrder.DESC) {
            builder.append("DESC");
        }
    }
}