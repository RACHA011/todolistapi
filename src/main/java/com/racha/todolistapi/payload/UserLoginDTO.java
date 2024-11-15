package com.racha.todolistapi.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDTO {

    @Email
    @Schema(example = "user@user.com", description = "Email of the user", requiredMode = RequiredMode.REQUIRED)
    private String email;

    @Size(min = 6, max = 25)
    @Schema(example = "password", description = "Password of the user", requiredMode = RequiredMode.REQUIRED)
    private String password;
}

