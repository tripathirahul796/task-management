package com.taskmanagement.repositories;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(String id);

    void deleteById(String id);

    List<Task> findAll(TaskStatus status, int page, int size);
}
