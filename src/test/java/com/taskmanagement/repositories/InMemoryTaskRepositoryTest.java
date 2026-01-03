package com.taskmanagement.repositories;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskRepositoryTest {
    private InMemoryTaskRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryTaskRepository();
    }

    @Test
    void saveAndFindById() {
        Task t = new Task("1","Title","D",TaskStatus.PENDING, LocalDate.now().plusDays(1));
        repo.save(t);

        assertTrue(repo.findById("1").isPresent());
        assertEquals("Title", repo.findById("1").get().getTitle());
    }

    @Test
    void findAll_sortedByDueDate() {
        Task t1 = new Task("1","A",null,TaskStatus.PENDING, LocalDate.now().plusDays(3));
        Task t2 = new Task("2","B",null,TaskStatus.PENDING, LocalDate.now().plusDays(1));
        Task t3 = new Task("3","C",null,TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(2));

        repo.save(t1);
        repo.save(t2);
        repo.save(t3);

        // No filter, page 0, size 10
        List<Task> all = repo.findAll(null, 0, 10);

        assertEquals(3, all.size());
        assertEquals("2", all.get(0).getId()); // earliest due date
        assertEquals("3", all.get(1).getId());
        assertEquals("1", all.get(2).getId());
    }

    @Test
    void findAll_withStatusFilter() {
        Task t1 = new Task("1","A",null,TaskStatus.PENDING, LocalDate.now().plusDays(3));
        Task t2 = new Task("2","B",null,TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(1));
        Task t3 = new Task("3","C",null,TaskStatus.PENDING, LocalDate.now().plusDays(2));

        repo.save(t1);
        repo.save(t2);
        repo.save(t3);

        List<Task> pendingTasks = repo.findAll(TaskStatus.PENDING, 0, 10);
        assertEquals(2, pendingTasks.size());
        assertEquals("3", pendingTasks.get(0).getId()); // dueDate sorted
        assertEquals("1", pendingTasks.get(1).getId());
    }

    @Test
    void findAll_pagination() {
        for (int i = 1; i <= 5; i++) {
            repo.save(new Task(String.valueOf(i), "T"+i, null, TaskStatus.PENDING, LocalDate.now().plusDays(i)));
        }

        List<Task> page0 = repo.findAll(null, 0, 2);
        List<Task> page1 = repo.findAll(null, 1, 2);
        List<Task> page2 = repo.findAll(null, 2, 2);

        assertEquals(2, page0.size());
        assertEquals("1", page0.get(0).getId());
        assertEquals("2", page0.get(1).getId());

        assertEquals(2, page1.size());
        assertEquals("3", page1.get(0).getId());
        assertEquals("4", page1.get(1).getId());

        assertEquals(1, page2.size());
        assertEquals("5", page2.get(0).getId());
    }
}
