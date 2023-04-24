package org.ilms.repository;

import lombok.extern.slf4j.Slf4j;
import org.ilms.repository.querybuilder.CaseQueryBuilder;
import org.ilms.repository.querybuilder.HearingQueryBuilder;
import org.ilms.repository.rowmapper.HearingRowMapper;
import org.ilms.repository.rowmapper.PartyRowMapper;
import org.ilms.web.model.Hearing;
import org.ilms.web.model.HearingResponse;
import org.ilms.web.model.HearingSearchCriteria;
import org.ilms.web.model.Party;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class HearingRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HearingQueryBuilder hearingQueryBuilder;

    @Autowired
    private HearingRowMapper hearingRowMapper;

    @Autowired
    private PartyRowMapper partyRowMapper;

    @Autowired
    private CaseQueryBuilder caseQueryBuilder;

    @Autowired
    private CaseRepository caseRepository;

    public HearingResponse getHearingDetails(HearingSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = hearingQueryBuilder.getHearingSearchQuery(criteria, preparedStmtList);
        List<Hearing> hearingDetails = jdbcTemplate.query(query, preparedStmtList.toArray(), hearingRowMapper);
        for (Hearing singleHearing : hearingDetails) {
            List<Party> partyList = getHearing(singleHearing.getCaseId());
            List<Party> party1 = new ArrayList<>();
            for (Party party : partyList) {
                party1.add(party);
                singleHearing.setParties(party1);
            }
        }
        HearingResponse hearingResponse = HearingResponse.builder().hearingList(hearingDetails).totalCount(hearingRowMapper.getFullCount())
                .build();
        return hearingResponse;
    }

    public List<Party> getHearing(String caseId) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(caseId);
        List<Party> parties = jdbcTemplate.query(caseQueryBuilder.getPartyQuery(), preparedStmtList.toArray(), partyRowMapper);
        return parties;
    }

    public String getMaxValueOfHearing(String caseId) {
        int value = 1;
        String finalValue = null;
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(caseId);
        List<String> maxHearingValue = jdbcTemplate.query(hearingQueryBuilder.getMaxHearingQuery(), preparedStmtList.toArray(),
                new SingleColumnRowMapper<>(String.class));
        try {
            if (maxHearingValue != null) {
                value = Integer.parseInt(maxHearingValue.get(0));
                finalValue = Integer.toString(value + 1);
            }
        } catch (Exception e) {
            finalValue = Integer.toString(value);
        }
        return finalValue;
    }

    public String getTenantIdFromHearing(String id) {

        List<String> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(id);
        List<String> tenantId = jdbcTemplate.query(hearingQueryBuilder.getTenantIdFromHearingQuery(), preparedStmtList.toArray(),
                new SingleColumnRowMapper<>(String.class));

        return tenantId.get(0);
    }

    public List<Party> getGetFromPartyQuery(String caseId) {
        List<Party> partyList = caseRepository.getParty(caseId);
        return partyList;
    }

}
