package org.ilms.web.controller;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.ilms.service.HearingService;
import org.ilms.util.ResponseInfoFactory;
import org.ilms.web.model.Hearing;
import org.ilms.web.model.HearingRequest;
import org.ilms.web.model.HearingResponse;
import org.ilms.web.model.HearingSearchCriteria;
import org.ilms.web.model.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/hearing")
@CrossOrigin (origins = "*", allowedHeaders = "*")
public class HearingController {
    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private HearingService hearingService;

    @PostMapping (value = "/_create")
    public ResponseEntity<HearingResponse> create(@Valid @RequestBody HearingRequest hearingRequest) {
        Hearing hearing = hearingService.create(hearingRequest);
        List<Hearing> hearingList = new ArrayList<Hearing>();
        hearingList.add(hearing);
        HearingResponse response = HearingResponse.builder().hearingList(hearingList).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(hearingRequest.getRequestInfo(), true)).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping (value = "/_search")
    public ResponseEntity<HearingResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
            @Valid @ModelAttribute HearingSearchCriteria criteria) {
        HearingResponse response = hearingService.hearingSearch(criteria, requestInfoWrapper.getRequestInfo());
        response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping (value = "/_update")
    public ResponseEntity<HearingResponse> update(@Valid @RequestBody HearingRequest hearingDetailsRequest) {
        Hearing hearingDetails = hearingService.update(hearingDetailsRequest);
        List<Hearing> hearingDetailsList = new ArrayList<Hearing>();
        hearingDetailsList.add(hearingDetails);
        HearingResponse response = HearingResponse.builder().hearingList(hearingDetailsList).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(hearingDetailsRequest.getRequestInfo(), true)).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
