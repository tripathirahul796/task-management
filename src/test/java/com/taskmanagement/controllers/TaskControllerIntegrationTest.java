package com.taskmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.TaskStatus;
import com.taskmanagement.dto.request.CreateTaskRequest;
import com.taskmanagement.dto.request.UpdateTaskRequest;
import com.taskmanagement.repositories.InMemoryTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryTaskRepository repository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.clear();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void createTask_valid_returns201() throws Exception {
        CreateTaskRequest req = new CreateTaskRequest();
        req.setTitle("Task 1");
        req.setDescription("Desc 1");
        req.setDueDate(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createTask_missingTitle_returns400() throws Exception {
        CreateTaskRequest req = new CreateTaskRequest();
        req.setDueDate(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getTask_existing_returns200() throws Exception {
       repository.save(new Task(
                "1", "Test", "Desc", TaskStatus.PENDING, LocalDate.now().plusDays(2)
        ));

        mockMvc.perform(get("/tasks/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Test"));
    }

    @Test
    void getTask_missing_returns404() throws Exception {
        mockMvc.perform(get("/tasks/{id}", "no-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with id: no-id"));
    }

    @Test
    void updateTask_existing_returns200() throws Exception {
        repository.save(new Task("1", "Old", "Desc", TaskStatus.PENDING, LocalDate.now().plusDays(2)));

        UpdateTaskRequest req = new UpdateTaskRequest();
        req.setTitle("New");
        req.setStatus(TaskStatus.DONE);

        mockMvc.perform(put("/tasks/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New"))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void updateTask_missing_returns404() throws Exception {
        UpdateTaskRequest req = new UpdateTaskRequest();
        req.setTitle("New");

        mockMvc.perform(put("/tasks/{id}", "no-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with id: no-id"));
    }

    @Test
    void deleteTask_existing_returns204() throws Exception {
        repository.save(new Task("1", "Delete", null, TaskStatus.PENDING, LocalDate.now().plusDays(1)));

        mockMvc.perform(delete("/tasks/{id}", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_missing_returns404() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", "no-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with id: no-id"));
    }

    @Test
    void listTasks_returnsSortedFiltered() throws Exception {
        repository.save(new Task("1", "A", null, TaskStatus.PENDING, LocalDate.now().plusDays(3)));
        repository.save(new Task("2", "B", null, TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(1)));
        repository.save(new Task("3", "C", null, TaskStatus.PENDING, LocalDate.now().plusDays(2)));

        // List all, sorted by dueDate
        mockMvc.perform(get("/tasks")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("2"))
                .andExpect(jsonPath("$[1].id").value("3"))
                .andExpect(jsonPath("$[2].id").value("1"));

        // Filter by PENDING
        mockMvc.perform(get("/tasks")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("3"))
                .andExpect(jsonPath("$[1].id").value("1"));
    }
}
