package com.taskmanagement.controllers;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.TaskStatus;
import com.taskmanagement.dto.request.CreateTaskRequest;
import com.taskmanagement.dto.request.UpdateTaskRequest;
import com.taskmanagement.dto.response.TaskResponse;
import com.taskmanagement.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) {
        Task task = toDomain(request);
        Task created = taskService.create(task);

        return ResponseEntity
                .created(URI.create("/tasks/" + created.getId()))
                .body(toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(@PathVariable("id") String id) {
        Task task = taskService.getById(id);
        return ResponseEntity.ok(toResponse(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateTaskRequest request) {

        Task updates = toDomain(request);
        Task updated = taskService.update(id, updates);

        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
public ResponseEntity<List<TaskResponse>> list(
         @RequestParam(name = "status", required = false) TaskStatus status,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size)  {

    List<Task> tasks = taskService.list(status, page, size);

    List<TaskResponse> response = tasks.stream()
            .map(this::toResponse)
            .toList();

    return ResponseEntity.ok(response);
}

    /* ---------- Mapping helpers ---------- */

    private Task toDomain(CreateTaskRequest request) {
        return new Task(
                null,
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getDueDate()
        );
    }

    private Task toDomain(UpdateTaskRequest request) {
        return new Task(
                null,
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getDueDate()
        );
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate()
        );
    }
}
