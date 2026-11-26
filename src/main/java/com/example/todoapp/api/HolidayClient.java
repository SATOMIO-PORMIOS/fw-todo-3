package com.example.todoapp.api;

import java.util.Map;
import java.util.Collections;
import java.net.http.HttpClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.JdkClientHttpRequestFactory;

@Component
public class HolidayClient {

    private static final String HOLIDAY_API_URL = "https://holidays-jp.github.io/api/v1/date.json";
    private final RestClient restClient;
    private boolean unavailable;

    public HolidayClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(3))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(java.time.Duration.ofSeconds(5));
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    public Map<String, String> getHolidays() {
        unavailable = false;
        try {
            Map<String, String> holidays = restClient.get()
                    .uri(HOLIDAY_API_URL)
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<Map<String, String>>() {});
            if (holidays == null) {
                unavailable = true;
                return Collections.emptyMap();
            }
            return holidays;
        } catch (org.springframework.web.client.RestClientException exception) {
            unavailable = true;
            return Collections.emptyMap();
        }
    }

    public boolean isUnavailable() {
        return unavailable;
    }
}
