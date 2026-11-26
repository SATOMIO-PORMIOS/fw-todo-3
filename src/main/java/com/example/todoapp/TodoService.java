package com.example.todoapp;

import java.util.List;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TodoService {

    private final TodoMapper todoMapper;

    public TodoService(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    public List<Todo> search(String keyword, String category, String order, LocalDate from, LocalDate to) {
        return todoMapper.search(keyword, category, order, from, to);
    }

    public List<Todo> search(String keyword, String category, String order) {
        return search(keyword, category, order, null, null);
    }

    public Todo findById(Long id) {
        return todoMapper.findById(id);
    }

    public int create(Todo todo) {
        int result = todoMapper.insert(todo);
        log.info("操作=登録, id={}", todo.getId());
        return result;
    }

    public int update(Todo todo) {
        int result = todoMapper.update(todo);
        log.info("操作=編集, id={}", todo.getId());
        return result;
    }

    public int delete(Long id) {
        int result = todoMapper.deleteById(id);
        log.info("操作=削除, id={}", id);
        return result;
    }
}
