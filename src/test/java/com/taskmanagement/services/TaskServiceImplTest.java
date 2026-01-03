package com.taskmanagement.services;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.TaskStatus;
import com.taskmanagement.exceptions.TaskNotFoundException;
import com.taskmanagement.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    private TaskRepository repository;
    private TaskServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(TaskRepository.class);
        service = new TaskServiceImpl(repository);
    }

    // -------------------- CREATE --------------------

    @Test
    void create_validTask_returnsSavedTask() {
        Task task = new Task(null, "Title", "Desc", TaskStatus.PENDING, LocalDate.now().plusDays(1));
        Task saved = new Task("1", "Title", "Desc", TaskStatus.PENDING, task.getDueDate());

        when(repository.save(any(Task.class))).thenReturn(saved);

        Task result = service.create(task);

        assertNotNull(result.getId());
        assertEquals("Title", result.getTitle());
        assertEquals(TaskStatus.PENDING, result.getStatus());
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    void create_missingDueDate_throwsException() {
        Task task = new Task(null, "Title", "Desc", TaskStatus.PENDING, null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(task));
        assertEquals("Due date is required", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void create_pastDueDate_throwsException() {
        Task task = new Task(null, "Title", "Desc", TaskStatus.PENDING, LocalDate.now().minusDays(1));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(task));
        assertEquals("Due date must be in the future", ex.getMessage());
        verify(repository, never()).save(any());
    }

    // -------------------- GET --------------------

    @Test
    void getById_existingTask_returnsTask() {
        Task t = new Task("1", "Title", "Desc", TaskStatus.PENDING, LocalDate.now().plusDays(1));
        when(repository.findById("1")).thenReturn(Optional.of(t));

        Task result = service.getById("1");

        assertEquals("1", result.getId());
        verify(repository, times(1)).findById("1");
    }

    @Test
    void getById_missingTask_throwsNotFound() {
        when(repository.findById("1")).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> service.getById("1"));
    }

    // -------------------- UPDATE --------------------

    @Test
    void update_existingTask_updatesFields() {
        Task existing = new Task("1", "Old", "OldDesc", TaskStatus.PENDING, LocalDate.now().plusDays(2));
        when(repository.findById("1")).thenReturn(Optional.of(existing));
        when(repository.save(any(Task.class))).thenReturn(existing);

        Task updates = new Task(null, "New", null, TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(3));
        Task result = service.update("1", updates);

        assertEquals("New", result.getTitle());
        assertEquals("OldDesc", result.getDescription()); // unchanged
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        assertEquals(LocalDate.now().plusDays(3), result.getDueDate());
        verify(repository, times(1)).save(existing);
    }

    @Test
    void update_missingTask_throwsNotFound() {
        when(repository.findById("1")).thenReturn(Optional.empty());
        Task updates = new Task(null, "New", null, TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(3));
        assertThrows(TaskNotFoundException.class, () -> service.update("1", updates));
    }

    @Test
    void update_invalidDueDate_throwsException() {
        Task existing = new Task("1", "Old", "Desc", TaskStatus.PENDING, LocalDate.now().plusDays(2));
        when(repository.findById("1")).thenReturn(Optional.of(existing));

        Task updates = new Task(null, null, null, null, LocalDate.now().minusDays(1));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.update("1", updates));
        assertEquals("Due date must be in the future", ex.getMessage());
        verify(repository, never()).save(any());
    }

    // -------------------- DELETE --------------------

    @Test
    void delete_existingTask_deletes() {
        Task t = new Task("1", "Title", "Desc", TaskStatus.PENDING, LocalDate.now().plusDays(1));
        when(repository.findById("1")).thenReturn(Optional.of(t));
        doNothing().when(repository).deleteById("1");

        service.delete("1");

        verify(repository, times(1)).deleteById("1");
    }

    @Test
    void delete_missingTask_throwsNotFound() {
        when(repository.findById("1")).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> service.delete("1"));
        verify(repository, never()).deleteById(any());
    }

    // -------------------- LIST --------------------

    @Test
    void list_returnsTasks() {
        Task t1 = new Task("1", "A", null, TaskStatus.PENDING, LocalDate.now().plusDays(1));
        Task t2 = new Task("2", "B", null, TaskStatus.PENDING, LocalDate.now().plusDays(2));
        when(repository.findAll(null, 0, 10)).thenReturn(List.of(t1, t2));

        List<Task> result = service.list(null, 0, 10);

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getId());
        verify(repository, times(1)).findAll(null, 0, 10);
    }
}
