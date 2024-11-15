package com.racha.todolistapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.racha.todolistapi.model.Account;
import com.racha.todolistapi.model.Task;
import com.racha.todolistapi.payload.task.TaskDTO;
import com.racha.todolistapi.payload.task.TaskUpdateDTO;
import com.racha.todolistapi.payload.task.TaskViewDTO;
import com.racha.todolistapi.service.AccountService;
import com.racha.todolistapi.service.TaskService;
import com.racha.todolistapi.util.contants.task.TaskEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
@Tag(name = "Task Controller", description = "Controller for Task management")
@Slf4j
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/task")
    @Operation(summary = "view my tasks")
    @ApiResponse(responseCode = "200", description = "my task")
    @ApiResponse(responseCode = "401", description = "forbidden")
    @ApiResponse(responseCode = "403", description = "Unauthorized access")
    @SecurityRequirement(name = "todo-list01-api")
    public List<TaskViewDTO> getMyTask(Authentication authentication) {
        Account account = validUser(authentication);
        List<Task> allTask = taskService.findAll();
        List<TaskViewDTO> allMyTasks = new ArrayList<>();

        for (Task task : allTask) {
            if (task.getAccount() == account) {
                if (!task.getStatus().toLowerCase().equals("completed")) {
                    allMyTasks.add(new TaskViewDTO(task.getId(), task.getTitle(), task.getDescription(),
                            task.getDueDate(), task.getPriority(), task.getStatus(), task.getTeamProfileId(),
                            task.getLabels()));
                }
            }
        }
        return allMyTasks;
    }

    @GetMapping("/task/all")
    @Operation(summary = "view all my tasks(including completed ones")
    @ApiResponse(responseCode = "200", description = "my task")
    @ApiResponse(responseCode = "401", description = "forbidden")
    @ApiResponse(responseCode = "403", description = "Unauthorized access")
    @SecurityRequirement(name = "todo-list01-api")
    public List<TaskViewDTO> getAllMyTask(Authentication authentication) {
        Account account = validUser(authentication);
        List<Task> allTask = taskService.findAll();
        List<TaskViewDTO> allMyTasks = new ArrayList<>();

        for (Task task : allTask) {
            if (task.getAccount() == account) {
                allMyTasks.add(new TaskViewDTO(task.getId(), task.getTitle(), task.getDescription(),
                        task.getDueDate(), task.getPriority(), task.getStatus(), task.getTeamProfileId(),
                        task.getLabels()));
            }
        }
        return allMyTasks;
    }

    @PostMapping(value = "/task/add", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Add a new task")
    @ApiResponse(responseCode = "200", description = "task created successfully")
    @ApiResponse(responseCode = "401", description = "forbidden")
    @ApiResponse(responseCode = "403", description = "Unauthorized access")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<String> addTask(@Valid @RequestBody TaskDTO taskDTO, Authentication authentication) {
        try {
            Task task = new Task();
            // map taskDTO to task
            task.setTitle(taskDTO.getTitle());
            task.setDescription(taskDTO.getDescription());
            task.setDueDate(taskDTO.getDueDate());
            task.setPriority(taskDTO.getPriority());
            task.setStatus(taskDTO.getStatus());
            task.setAccount(validUser(authentication));
            task.setLabels(taskDTO.getLabels());
            // it should be true or false
            task.setTeamwork(taskDTO.isTeamwork());
            task.setTeamProfileId(taskDTO.getTeamProfileId());

            taskService.save(task);
            return new ResponseEntity<>(Long.toString(task.getId()), HttpStatus.OK);
        } catch (Exception e) {
            log.debug("Adding Task failed: " + e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/task/{task_id}/view", produces = "application/json")
    @Operation(summary = "view task")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<TaskViewDTO> viewTask(@PathVariable("task_id") Long id, Authentication authentication) {
        Task task = taskService.findById(id).get();
        if (!validUser(authentication).equals(task.getAccount())) {
            return new ResponseEntity<>(new TaskViewDTO(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(new TaskViewDTO(task.getId(), task.getTitle(), task.getDescription(),
                task.getDueDate(), task.getPriority(), task.getStatus(), task.getTeamProfileId(), task.getLabels()),
                HttpStatus.OK);
    }

    @PutMapping(value = "/task/{task_id}/update", consumes = "application/json", produces = "application/json")
    @Operation(summary = "update a task")
    @ApiResponse(responseCode = "200", description = "task updated successfully")
    @ApiResponse(responseCode = "401", description = "forbidden")
    @ApiResponse(responseCode = "403", description = "Unauthorized access")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<String> updateTask(@PathVariable("task_id") Long id,
            @Valid @RequestBody TaskUpdateDTO taskDTO,
            Authentication authentication) {
        try {
            Task task = taskService.findById(id).get();
            if (task.getAccount().equals(validUser(authentication))) {
                task.setTitle(taskDTO.getTitle());
                task.setDescription(taskDTO.getDescription());
                task.setDueDate(taskDTO.getDueDate());
                task.setPriority(taskDTO.getPriority());
                task.setStatus(taskDTO.getStatus());
                task.setLabels(taskDTO.getLabels());

                if (task.isTeamwork()) {
                    task.setTeamProfileId(taskDTO.getTeamProfileId());
                }

                taskService.save(task);
                log.debug("task updated successfully");
                return new ResponseEntity<>(TaskEnum.TASK_UPDATED.toString(), HttpStatus.OK);
            }
            return new ResponseEntity<>("Unauthorized access", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.debug("Adding Task failed: " + e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/task/{task_id}/delete")
    @Operation(summary = "delete a task")
    @ApiResponse(responseCode = "200", description = "task deleted successfully")
    @ApiResponse(responseCode = "401", description = "forbidden")
    @ApiResponse(responseCode = "403", description = "Unauthorized access")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<String> deleteTask(@PathVariable("task_id") Long id, Authentication authentication) {

        Task task = taskService.findById(id).orElse(null);
        if (task == null) {
            return new ResponseEntity<>("Task not found", HttpStatus.NOT_FOUND);
        }
        if (!validUser(authentication).equals(task.getAccount())) {
            return new ResponseEntity<>("unauthorized access", HttpStatus.UNAUTHORIZED);
        }
        taskService.delete(task);
        return new ResponseEntity<>(TaskEnum.TASK_REMOVED.toString(), HttpStatus.OK);
    }

    private Account validUser(Authentication authentication) {
        String email = authentication.getName();

        Optional<Account> optionalAccount = accountService.findByEmail(email);

        return optionalAccount.get();
    }

}
