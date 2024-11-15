package com.racha.todolistapi.payload.task;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskViewDTO {
    private Long id;

    private String title;

    private String description;

    private LocalDate dueDate;

    private String priority; // (e.g., "high", "medium", "low")

    private String status; // (e.g., "pending", "completed")

    private List<Long> teamProfileId;

    private List<String> labels;
}
