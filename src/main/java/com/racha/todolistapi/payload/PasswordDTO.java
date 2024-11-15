package com.racha.todolistapi.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDTO {

    @Size(min = 6, max = 25)
    @Schema(example = "password", description = "Password of the user", requiredMode = RequiredMode.REQUIRED)
    private String password;
}
