package org.ilms.web.controller;

import lombok.extern.log4j.Log4j2;
import org.ilms.service.CaseService;
import org.ilms.util.ResponseInfoFactory;
import org.ilms.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/case")
@Log4j2
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CaseController {
    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private CaseService caseService;

    @PostMapping(value = "/_search")
    public ResponseEntity<CaseResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                               @Valid @ModelAttribute CaseSearchCriteria criteria) {
        log.info("ILMSCaseController :: search() : START ");
        CaseResponse response = caseService.ilmsCaseSearch(criteria, requestInfoWrapper.getRequestInfo(), requestInfoWrapper.getProcessSearchCriteria());
        response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true));
        log.info("ILMSCaseController :: search() : END With Response [ " + response + " ]");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/_create")
    public ResponseEntity<CaseResponse> create(@Valid @RequestBody CaseRequest caseRequest) {
        Case caseObj = caseService.create(caseRequest);
        List<Case> caseList = new ArrayList<Case>();
        caseList.add(caseObj);
        CaseResponse response = CaseResponse.builder().caseList(caseList)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(caseRequest.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/_update")
    public ResponseEntity<CaseResponse> update(@Valid @RequestBody CaseRequest caseRequest) {
        CaseResponse response = new CaseResponse();
        Case caseObj = caseService.update(caseRequest);
        List<Case> caseList = new ArrayList<>();
        caseList.add(caseObj);
        response.setCaseList(caseList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/_caseDetails")
    public ResponseEntity<CaseDetailsResponse> CaseDetailsResponse(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                                   @Valid @ModelAttribute CaseSearchCriteria criteria) {
        log.info("ILMSCaseController :: search() : START ");
        CaseDetailsResponse downloadResponse = caseService.caseDetailsSearch(criteria, requestInfoWrapper.getRequestInfo());
        downloadResponse.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true));
        log.info("ILMSCaseController :: search() : END With Response [ " + downloadResponse + " ]");
        return new ResponseEntity<>(downloadResponse, HttpStatus.OK);
    }
}
