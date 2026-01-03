package com.taskmanagement.services;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.TaskStatus;
import com.taskmanagement.exceptions.TaskNotFoundException;
import com.taskmanagement.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    public TaskServiceImpl(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Task create(Task task) {
        validateDueDate(task.getDueDate());

        Task toSave = new Task(
                UUID.randomUUID().toString(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus() != null ? task.getStatus() : TaskStatus.PENDING,
                task.getDueDate()
        );

        return repository.save(toSave);
    }

    @Override
    public Task getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    public Task update(String id, Task updates) {
        Task existing = getById(id);

        if (updates.getTitle() != null) {
            existing.setTitle(updates.getTitle());
        }
        if (updates.getDescription() != null) {
            existing.setDescription(updates.getDescription());
        }
        if (updates.getStatus() != null) {
            existing.setStatus(updates.getStatus());
        }
        if (updates.getDueDate() != null) {
            validateDueDate(updates.getDueDate());
            existing.setDueDate(updates.getDueDate());
        }

        return repository.save(existing);
    }

    @Override
    public void delete(String id) {
        Task existing = getById(id);
        repository.deleteById(existing.getId());
    }

    @Override
    public List<Task> list(TaskStatus status, int page, int size) {
        return repository.findAll(status, page, size);
    }
    

    private void validateDueDate(LocalDate dueDate) {
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date is required");
        }
        if (!dueDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Due date must be in the future");
        }
    }
}
