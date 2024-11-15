package com.racha.todolistapi.payload.task;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    @Schema(example = "Eat", description = "Title of the task", requiredMode = RequiredMode.REQUIRED)
    private String title;

    @Schema(example = "Eating", description = "Description of the task")
    private String description;

    @Schema(example = "2024-12-31", description = "Due date and time of the task", requiredMode = RequiredMode.REQUIRED)
    private LocalDate dueDate;

    @Schema(example = "high", description = "Priority of the task")
    private String priority; // (e.g., "high", "medium", "low")

    @Schema(example = "pending", description = "Status of the task")
    private String status; // (e.g., "pending", "completed")

    @Schema(example = "false", description = "Is this task a teamwork")
    private boolean teamwork; // if whether its a team work or not

    @Schema(description = "team ids")
    private List<Long> teamProfileId; // Optional

    @Schema(description = "Labels associated with the task")
    private List<String> labels;
}
