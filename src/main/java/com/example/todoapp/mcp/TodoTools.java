package com.example.todoapp.mcp;

import com.example.todoapp.Todo;
import com.example.todoapp.TodoService;
import com.example.todoapp.api.HolidayClient;
import com.example.todoapp.api.TodoDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class TodoTools {

    private final TodoService todoService;
    private final HolidayClient holidayClient;

    public TodoTools(TodoService todoService, HolidayClient holidayClient) {
        this.todoService = todoService;
        this.holidayClient = holidayClient;
    }

    @McpTool(name = "list_todos", description = "やることの一覧を返す（期間・ジャンルで絞れる）")
    public List<TodoDto> listTodos(
            @McpToolParam(description = "やることのタイトルに含むキーワード", required = false) String keyword,
            @McpToolParam(description = "ジャンル", required = false) String category,
            @McpToolParam(description = "期間の開始日（yyyy-MM-dd）", required = false) String from,
            @McpToolParam(description = "期間の終了日（yyyy-MM-dd）", required = false) String to) {
        LocalDate fromDate = from == null ? null : LocalDate.parse(from);
        LocalDate toDate = to == null ? null : LocalDate.parse(to);
        List<Todo> todos = todoService.search(keyword, category, "asc", fromDate, toDate);
        return todos.stream().map(TodoDto::from).collect(Collectors.toList());
    }

    @McpTool(name = "get_todo", description = "やることを1件返す")
    public TodoDto getTodo(
            @McpToolParam(description = "やることのID", required = true) Long id) {
        Todo todo = todoService.findById(id);
        return todo == null ? null : TodoDto.from(todo);
    }

    @McpTool(name = "add_todo", description = "やることを1件足す")
    public TodoDto addTodo(
            @McpToolParam(description = "やることのタイトル", required = true) String title,
            @McpToolParam(description = "詳細", required = false) String detail,
            @McpToolParam(description = "ジャンル（旅行・グルメ・買い物・体験・学習）", required = true) String category,
            @McpToolParam(description = "優先度", required = true) Integer priority,
            @McpToolParam(description = "期限（yyyy-MM-dd）", required = false) String dueDate,
            @McpToolParam(description = "予算額（円、0以上の整数）", required = false) Long budgetAmount,
            @McpToolParam(description = "実際の金額（円、0以上の整数）", required = false) Long actualAmount) {
        if (!isValidCategory(category)) {
            throw new IllegalArgumentException("ジャンルは指定された5種類から選んでください。");
        }
        validateAmounts(budgetAmount, actualAmount);
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDetail(detail);
        todo.setCategory(category);
        todo.setPriority(priority);
        todo.setDueDate(dueDate == null ? null : LocalDate.parse(dueDate));
        todo.setBudgetAmount(budgetAmount);
        todo.setActualAmount(actualAmount);
        todo.setCompleted(false);
        todoService.create(todo);
        return TodoDto.from(todo);
    }

    @McpTool(name = "update_todo", description = "やることを1件直す（期限を変えるのもこれ）")
    public TodoDto updateTodo(
            @McpToolParam(description = "やることのID", required = true) Long id,
            @McpToolParam(description = "新しいタイトル", required = false) String title,
            @McpToolParam(description = "新しい詳細", required = false) String detail,
            @McpToolParam(description = "新しいジャンル（旅行・グルメ・買い物・体験・学習）", required = false) String category,
            @McpToolParam(description = "新しい優先度", required = false) Integer priority,
            @McpToolParam(description = "新しい期限（yyyy-MM-dd）", required = false) String dueDate,
            @McpToolParam(description = "新しい予算額（円、0以上の整数）", required = false) Long budgetAmount,
            @McpToolParam(description = "新しい実際の金額（円、0以上の整数）", required = false) Long actualAmount) {
        Todo todo = todoService.findById(id);
        if (todo == null) return null;
        if (category != null && !isValidCategory(category)) {
            throw new IllegalArgumentException("ジャンルは指定された5種類から選んでください。");
        }
        validateAmounts(budgetAmount, actualAmount);
        if (title != null) todo.setTitle(title);
        if (detail != null) todo.setDetail(detail);
        if (category != null) todo.setCategory(category);
        if (priority != null) todo.setPriority(priority);
        if (dueDate != null) todo.setDueDate(LocalDate.parse(dueDate));
        if (budgetAmount != null) todo.setBudgetAmount(budgetAmount);
        if (actualAmount != null) todo.setActualAmount(actualAmount);
        todoService.update(todo);
        return TodoDto.from(todo);
    }

    @McpTool(name = "complete_todo", description = "やることを完了にする")
    public TodoDto completeTodo(
            @McpToolParam(description = "やることのID", required = true) Long id) {
        Todo todo = todoService.findById(id);
        if (todo == null) return null;
        todo.setCompleted(true);
        todoService.update(todo);
        return TodoDto.from(todo);
    }

    @McpTool(name = "delete_todo", description = "やることを1件消す")
    public int deleteTodo(
            @McpToolParam(description = "やることのID", required = true) Long id) {
        return todoService.delete(id);
    }

    @McpTool(name = "find_free_days", description = "期間の中で、期限のやることが無く、土日でも祝日でもない「空いている日」を返す。やることの期限を動かす先を決めるのに使う")
    public List<LocalDate> findFreeDays(
            @McpToolParam(description = "期間の開始日（yyyy-MM-dd）", required = true) String from,
            @McpToolParam(description = "期間の終了日（yyyy-MM-dd）", required = true) String to) {
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        if (toDate.isBefore(fromDate)) return List.of();

        Set<LocalDate> occupiedDates = todoService.search(null, null, "asc", fromDate, toDate).stream()
                .map(Todo::getDueDate)
                .filter(date -> date != null)
                .collect(Collectors.toSet());
        Map<String, String> holidays = holidayClient.getHolidays();
        Set<LocalDate> holidayDates = new HashSet<>();
        for (String date : holidays.keySet()) holidayDates.add(LocalDate.parse(date));

        List<LocalDate> freeDays = new ArrayList<>();
        for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
            boolean weekend = date.getDayOfWeek().getValue() >= 6;
            if (!occupiedDates.contains(date) && !weekend && !holidayDates.contains(date)) {
                freeDays.add(date);
            }
        }
        return freeDays;
    }

    private boolean isValidCategory(String category) {
        return Arrays.asList("旅行", "グルメ", "買い物", "体験", "学習")
                .contains(category);
    }

    private void validateAmounts(Long budgetAmount, Long actualAmount) {
        if (budgetAmount != null && budgetAmount < 0) {
            throw new IllegalArgumentException("予算額は0円以上で入力してください。");
        }
        if (actualAmount != null && actualAmount < 0) {
            throw new IllegalArgumentException("実際の金額は0円以上で入力してください。");
        }
    }
}
