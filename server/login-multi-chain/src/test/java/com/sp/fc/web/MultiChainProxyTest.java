package com.sp.fc.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.fc.web.student.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MultiChainProxyTest {

    @LocalServerPort
    int port;

    RestTemplate restTemplate = new RestTemplate();

    TestRestTemplate testClient = new TestRestTemplate("choi", "1");

    @DisplayName("2. 학생 리스트를 조회한다")
    @Test
    void test_2() {

        ResponseEntity<List<Student>> response = testClient.exchange("http://localhost:" + port + "/api/teacher/students",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Student>>() {
                });

        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        System.out.println(response.getBody());
    }

    @DisplayName("1. 학생 조사")
    @Test
    void test_1() throws JsonProcessingException {
        String url = format("http://localhost:%d/api/teacher/students", port);

        System.out.println("url: " + url);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic "+ Base64.getEncoder().encodeToString(
                "choi:1".getBytes()
        ));
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        // Return serialized json data
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        System.out.println("getBody(): " + response.getBody());

        // Deserializing into list object
        List<Student> list = new ObjectMapper().readValue(response.getBody(),
                new TypeReference<List<Student>>() {
                });

        System.out.println(list);
        assertEquals(3, list.size());
    }
}
