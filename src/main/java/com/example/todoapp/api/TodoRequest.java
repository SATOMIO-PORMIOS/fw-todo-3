package com.example.todoapp.api;

import com.example.todoapp.Todo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

public class TodoRequest {
    @NotBlank(message = "やることを入力してください")
    @Size(max = 255, message = "やることは255文字以内で入力してください")
    private String title;

    @Size(max = 255, message = "メモは255文字以内で入力してください")
    private String detail;

    @NotBlank(message = "ジャンルを選んでください")
    private String category;

    @NotNull(message = "優先度を選んでください")
    private Integer priority;

    private LocalDate dueDate;
    @PositiveOrZero(message = "予算額は0円以上で入力してください")
    private Long budgetAmount;
    @PositiveOrZero(message = "実際の金額は0円以上で入力してください")
    private Long actualAmount;
    private Boolean completed;

    public Todo toTodo() {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDetail(detail);
        todo.setCategory(category);
        todo.setPriority(priority);
        todo.setDueDate(dueDate);
        todo.setBudgetAmount(budgetAmount);
        todo.setActualAmount(actualAmount);
        todo.setCompleted(completed);
        return todo;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public Long getBudgetAmount() { return budgetAmount; }
    public void setBudgetAmount(Long budgetAmount) { this.budgetAmount = budgetAmount; }
    public Long getActualAmount() { return actualAmount; }
    public void setActualAmount(Long actualAmount) { this.actualAmount = actualAmount; }
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
}
