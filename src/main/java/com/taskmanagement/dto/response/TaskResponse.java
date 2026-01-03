package com.taskmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taskmanagement.domain.TaskStatus;

import java.time.LocalDate;

public class TaskResponse {
    private String id;
    private String title;
    private String description;
    private TaskStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    public TaskResponse() {
    }

    public TaskResponse(String id, String title, String description, TaskStatus status, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
