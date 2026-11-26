package com.example.todoapp.api;

import com.example.todoapp.Todo;
import com.example.todoapp.TodoService;
import java.time.LocalDate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class TodoApiController {

    private final TodoService todoService;

    public TodoApiController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/api/todos")
    public List<TodoDto> getTodos(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        List<Todo> todos = todoService.search(keyword, category, order, from, to);
        return todos.stream().map(TodoDto::from).collect(Collectors.toList());
    }

    @GetMapping("/api/todos/{id}")
    public ResponseEntity<TodoDto> getTodo(@PathVariable Long id) {
        Todo todo = todoService.findById(id);
        if (todo == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(TodoDto.from(todo));
    }

    @PostMapping("/api/todos")
    public ResponseEntity<TodoDto> createTodo(@Valid @RequestBody TodoRequest request) {
        Todo todo = request.toTodo();
        if (todo.getCompleted() == null) todo.setCompleted(false);
        todoService.create(todo);
        Todo createdTodo = todoService.findById(todo.getId());
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/todos/{id}").buildAndExpand(todo.getId()).toUri())
                .body(TodoDto.from(createdTodo));
    }

    @PutMapping("/api/todos/{id}")
    public ResponseEntity<TodoDto> updateTodo(@PathVariable Long id, @Valid @RequestBody TodoRequest request) {
        if (todoService.findById(id) == null) return ResponseEntity.notFound().build();
        Todo todo = request.toTodo();
        todo.setId(id);
        todoService.update(todo);
        return ResponseEntity.ok(TodoDto.from(todoService.findById(id)));
    }

    @DeleteMapping("/api/todos/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        if (todoService.findById(id) == null) return ResponseEntity.notFound().build();
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationError(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<ValidationFieldError> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationFieldError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(new ValidationErrorResponse(
                "about:blank", "Bad Request", 400, "入力に誤りがあります", request.getRequestURI(), errors));
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationErrorResponse> handleInvalidRequestBody(HttpServletRequest request) {
        return ResponseEntity.badRequest().body(new ValidationErrorResponse(
                "about:blank", "Bad Request", 400, "入力に誤りがあります", request.getRequestURI(), new ArrayList<>()));
    }

    public record ValidationErrorResponse(String type, String title, int status, String detail,
                                          String instance, List<ValidationFieldError> errors) {}

    public record ValidationFieldError(String field, String message) {}
}
