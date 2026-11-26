package com.example.todoapp;

import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalendarController {

    @GetMapping("/calendar")
    public String calendar(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {
        YearMonth displayedMonth = (year == null || month == null)
                ? YearMonth.now()
                : YearMonth.of(year, month);
        LocalDate firstDay = displayedMonth.atDay(1);
        LocalDate lastDay = displayedMonth.atEndOfMonth();

        model.addAttribute("year", displayedMonth.getYear());
        model.addAttribute("month", displayedMonth.getMonthValue());
        model.addAttribute("firstDay", firstDay);
        model.addAttribute("lastDay", lastDay);
        model.addAttribute("previousMonth", displayedMonth.minusMonths(1));
        model.addAttribute("nextMonth", displayedMonth.plusMonths(1));
        model.addAttribute("daysInMonth", displayedMonth.lengthOfMonth());
        model.addAttribute("leadingBlankDays", firstDay.getDayOfWeek().getValue() % 7);
        model.addAttribute("trailingBlankDays", (7 - (displayedMonth.lengthOfMonth() + firstDay.getDayOfWeek().getValue() % 7) % 7) % 7);
        return "calendar";
    }
}
