package com.momo.savanger.integration.web;

import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public abstract class BaseControllerIt {

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected MultiValueMap<String, String> params;

    protected List<Object> pathVariables;

//    @MockBean
//    private Drive mockDrive;

    @BeforeEach
    public void initBaseController() {
        this.params = new LinkedMultiValueMap<>();
        this.pathVariables = new ArrayList<>();
    }

    protected void getOK(String endpoint, ResultMatcher... matchers) throws Exception {
        this.get(endpoint, HttpStatus.OK, matchers);
    }

    protected void get(String endpoint, HttpStatus status, ResultMatcher... matchers)
            throws Exception {
        this.get(endpoint, status, new HttpHeaders(), matchers);
    }

    protected void get(String endpoint, HttpStatus status, HttpHeaders headers,
            ResultMatcher... matchers) throws Exception {
        this.mvc.perform(MockMvcRequestBuilders
                        .get(endpoint, this.pathVariables.toArray())
                        .params(this.params)
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().is(status.value()))
                .andExpect(matchAll(matchers));
    }

    protected void postOK(String endpoint, Object body, ResultMatcher... matchers)
            throws Exception {
        this.post(endpoint, body, HttpStatus.OK, matchers);
    }

    protected void post(String endpoint, Object body, HttpStatus status, ResultMatcher... matchers)
            throws Exception {
        this.post(endpoint, body, status, new HttpHeaders(), matchers);
    }

    protected void post(String endpoint, Object body, HttpStatus status, HttpHeaders headers,
            ResultMatcher... matchers) throws Exception {
        this.mvc.perform(MockMvcRequestBuilders
                        .post(endpoint, this.pathVariables.toArray())
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .content(this.writeObject(body))
                ).andExpect(status().is(status.value()))
                .andExpect(matchAll(matchers));
    }

    protected void putOK(String endpoint, Object body, ResultMatcher... matchers) throws Exception {
        this.put(endpoint, body, HttpStatus.OK, matchers);
    }

    protected void put(String endpoint, Object body, HttpStatus status, ResultMatcher... matchers)
            throws Exception {
        this.put(endpoint, body, status, new HttpHeaders(), matchers);
    }

    protected void put(String endpoint, Object body, HttpStatus status, HttpHeaders headers,
            ResultMatcher... matchers) throws Exception {
        this.mvc.perform(MockMvcRequestBuilders
                        .put(endpoint, this.pathVariables.toArray())
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .content(this.writeObject(body))
                ).andExpect(status().is(status.value()))
                .andExpect(matchAll(matchers));
    }

    protected void deleteOK(String endpoint, ResultMatcher... matchers) throws Exception {
        this.delete(endpoint, HttpStatus.OK, matchers);
    }

    protected void delete(String endpoint, HttpStatus status, ResultMatcher... matchers)
            throws Exception {
        this.delete(endpoint, status, new HttpHeaders(), matchers);
    }

    protected void delete(String endpoint, HttpStatus status,
            HttpHeaders headers, ResultMatcher... matchers) throws Exception {
        this.mvc.perform(MockMvcRequestBuilders
                        .delete(endpoint, this.pathVariables.toArray())
                        .params(this.params)
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().is(status.value()))
                .andExpect(matchAll(matchers));
    }

    private String writeObject(Object object) throws JsonProcessingException {
        if (object != null) {
            return this.objectMapper.writeValueAsString(object);
        }

        return "";
    }
}
