package com.test.tms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Task {

    @Id
    @GeneratedValue(generator = "my_seq")
    @SequenceGenerator(name = "my_seq", sequenceName = "task_seq", allocationSize = 1)
    private int taskId;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private State state;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private String assignTo;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "task_id", referencedColumnName = "taskId")
    private List<SubTask> subTaskList = new ArrayList<>();

    @PrePersist
    public void setCreatedOn() {
        this.createdOn = LocalDateTime.now();
    }

    @PreUpdate
    public void setUpdatedOn() {
        this.updatedOn = LocalDateTime.now();
    }
}
