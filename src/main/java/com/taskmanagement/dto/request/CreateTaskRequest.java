package com.taskmanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taskmanagement.domain.TaskStatus;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateTaskRequest {

    @NotBlank(message = "title is required")
    private String title;

    private String description;

    private TaskStatus status = TaskStatus.PENDING;

    @NotNull(message = "due_date is required")
    @Future(message = "due_date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    public CreateTaskRequest() {
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

