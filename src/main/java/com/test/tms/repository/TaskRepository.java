package com.test.tms.repository;

import com.test.tms.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository <Task,Integer> {
    @Query("SELECT t FROM Task t WHERE t.title ILIKE %:keyword% OR t.description ILIKE %:keyword% OR t.assignTo ILIKE %:keyword%")
    List<Task> findByKeyword(@Param("keyword") String keyword);
}
