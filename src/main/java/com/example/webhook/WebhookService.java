package com.example.webhook;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class WebhookService {

    private final RestTemplate restTemplate;

    public WebhookService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public void executeFlow() {
        String registrationUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> request = new HashMap<>();
        request.put("name", "Arnav Sinha");
        request.put("regNo", "22BCE0830");
        request.put("email", "arnav@gmail.com");

        ResponseEntity<Map> response = restTemplate.postForEntity(registrationUrl, request, Map.class);
        Map body = response.getBody();

        String webhookUrl = null;
        if (body != null) {
            Object w1 = body.get("webhook");
            Object w2 = body.get("webhookUrl");
            Object w3 = body.get("url");
            webhookUrl = Objects.toString(w1 != null ? w1 : (w2 != null ? w2 : w3), null);
        }

        String accessToken = body != null ? Objects.toString(body.get("accessToken"), null) : null;

        boolean isEven = isEvenRegNo("22BCE0830");

        String finalQuery = isEven ? getEvenQuestionFinalQuery() : getOddQuestionPlaceholder();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (accessToken != null && !accessToken.isEmpty()) {
            headers.set("Authorization", accessToken);
        }

        Map<String, String> payload = Map.of("finalQuery", finalQuery);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            restTemplate.postForEntity(webhookUrl, entity, String.class);
        } else {
            String fallback = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
            restTemplate.postForEntity(fallback, entity, String.class);
        }
    }

    private boolean isEvenRegNo(String regNo) {
        for (int i = regNo.length() - 1; i >= 0; i--) {
            char c = regNo.charAt(i);
            if (Character.isDigit(c)) {
                return ((c - '0') % 2) == 0;
            }
        }
        return false;
    }

    private String getEvenQuestionFinalQuery() {
        return "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, " +
               "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
               "FROM EMPLOYEE e1 " +
               "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID " +
               "LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT AND e2.DOB > e1.DOB " +
               "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME " +
               "ORDER BY e1.EMP_ID DESC;";
    }

    private String getOddQuestionPlaceholder() {
        return "/* TODO: Odd-regNo question final query goes here */ SELECT 1;";
    }
}
