package com.taskmanagement.repositories;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.TaskStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

@Repository
public class InMemoryTaskRepository implements TaskRepository {

    private final ConcurrentMap<String, Task> store = new ConcurrentHashMap<>();

    @Override
    public Task save(Task task) {
        store.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }

    @Override
    public List<Task> findAll(TaskStatus status, int page, int size) {
        Stream<Task> stream = store.values().stream();

        if (status != null) {
            stream = stream.filter(task -> task.getStatus() == status);
        }

        List<Task> sorted = stream
                .sorted(Comparator.comparing(Task::getDueDate))
                .toList();

        return paginate(sorted, page, size);
    }

    private List<Task> paginate(List<Task> tasks, int page, int size) {
        if (size <= 0) {
            return tasks;
        }

        int fromIndex = Math.max(0, page) * size;
        if (fromIndex >= tasks.size()) {
            return new ArrayList<>();
        }

        int toIndex = Math.min(tasks.size(), fromIndex + size);
        return tasks.subList(fromIndex, toIndex);
    }

    /**
     * Clears all tasks from the in-memory repository.
     * Useful for resetting state in integration tests.
     */
    public void clear() {
        store.clear();
    }
}
