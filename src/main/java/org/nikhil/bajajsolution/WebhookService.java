package org.nikhil.bajajsolution;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate;

    public WebhookService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public void startFlow() {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";


        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Nikhil Singhal");
        requestBody.put("regNo", "0967CS221042");
        requestBody.put("email", "nikhilsinghal476@gmail.com");

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String webhookUrl = (String) response.getBody().get("webhook");
                String accessToken = (String) response.getBody().get("accessToken");

                System.out.println("Webhook URL: " + webhookUrl);
                System.out.println("Access Token: [" + accessToken + "]");


                String finalQuery = getSqlProblem2Query();


                submitAnswer(webhookUrl, accessToken, finalQuery);
            } else {
                System.err.println("Failed to generate webhook: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error generating webhook: " + e.getStatusCode());
            System.err.println(e.getResponseBodyAsString());
        }
    }

    private String getSqlProblem2Query() {
        return """
               SELECT 
                   e1.EMP_ID,
                   e1.FIRST_NAME,
                   e1.LAST_NAME,
                   d.DEPARTMENT_NAME,
                   COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT
               FROM EMPLOYEE e1
               JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID
               LEFT JOIN EMPLOYEE e2 
                   ON e1.DEPARTMENT = e2.DEPARTMENT
                  AND e2.DOB > e1.DOB
               GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME
               ORDER BY e1.EMP_ID DESC;
               """;
    }

    private void submitAnswer(String webhookUrl, String accessToken, String finalQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, request, String.class);
            System.out.println("Submission Response: " + response.getBody());
        } catch (HttpClientErrorException e) {
            System.err.println("Error submitting SQL: " + e.getStatusCode());
            System.err.println(e.getResponseBodyAsString());
        }
    }
}
