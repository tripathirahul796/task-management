package com.taskmanagement.services;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.TaskStatus;

import java.util.List;

public interface TaskService {

    Task getById(String id);

    List<Task> list(TaskStatus status, int page, int size);

    Task create(Task task);

    Task update(String id, Task updates);

    void delete(String id);
}
