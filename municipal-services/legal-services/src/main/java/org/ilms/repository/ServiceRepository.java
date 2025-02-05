package org.ilms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class ServiceRepository {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    /**
     * Fetches results from a REST service using the uri and object
     */
    public Optional<Object> fetchResult(StringBuilder uri, Object request) {

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Object response = null;
        log.info("URI: " + uri.toString());
        try {
            log.info("Request: " + mapper.writeValueAsString(request));
            response = restTemplate.postForObject(uri.toString(), request, Map.class);
        } catch (HttpClientErrorException e) {

            log.error("External Service threw an Exception: ", e);
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {

            log.error("Exception while fetching from external service: ", e);
            throw new CustomException("REST_CALL_EXCEPTION : " + uri, e.getMessage());
        }
        return Optional.ofNullable(response);
    }

    public Object fetchUserResult(StringBuilder uri, Object request) {

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Object response = null;
        log.info("URI: " + uri.toString());
        try {
            log.info("Request: " + mapper.writeValueAsString(request));
            response = restTemplate.postForObject(uri.toString(), request, Map.class);
        } catch (HttpClientErrorException e) {

            log.error("External Service threw an Exception: ", e);
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {

            log.error("Exception while fetching from external service: ", e);
            throw new CustomException("REST_CALL_EXCEPTION : " + uri, e.getMessage());
        }
        return response;
    }

    public List fetchListResult(StringBuilder uri, Object request) {
        List response = null;
        try {
            response = restTemplate.postForObject(uri.toString(), request, List.class);
        } catch (HttpClientErrorException e) {
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new ServiceCallException(e.getMessage());
        }

        return response;
    }
}
