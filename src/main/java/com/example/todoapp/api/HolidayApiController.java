package com.example.todoapp.api;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolidayApiController {

    private final HolidayClient holidayClient;

    public HolidayApiController(HolidayClient holidayClient) {
        this.holidayClient = holidayClient;
    }

    @GetMapping("/api/holidays")
    public ResponseEntity<Map<String, String>> getHolidays(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        Map<String, String> holidays = holidayClient.getHolidays();
        if (from != null || to != null) {
            holidays = holidays.entrySet().stream()
                    .filter(entry -> {
                        LocalDate date = LocalDate.parse(entry.getKey());
                        return (from == null || !date.isBefore(from))
                                && (to == null || !date.isAfter(to));
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        if (holidayClient.isUnavailable()) {
            return ResponseEntity.ok()
                    .header("X-Holidays-Unavailable", "true")
                    .body(holidays);
        }
        return ResponseEntity.ok(holidays);
    }
}
