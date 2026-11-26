package com.example.todoapp;

import java.util.List;
import java.time.LocalDate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TodoMapper {
    List<Todo> search(@Param("keyword") String keyword, @Param("category") String category,
                      @Param("order") String order, @Param("sortBy") String sortBy,
                      @Param("from") LocalDate from, @Param("to") LocalDate to,
                      @Param("priority") Integer priority,
                      @Param("amountType") String amountType,
                      @Param("minAmount") Long minAmount,
                      @Param("maxAmount") Long maxAmount);
    Todo findById(Long id);
    int insert(Todo todo);
    int update(Todo todo);
    int deleteById(Long id);
}
