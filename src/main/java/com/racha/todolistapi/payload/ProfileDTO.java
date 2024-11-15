package com.racha.todolistapi.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {

    // @Schema(example = "username", description = "Your username")
    private String username;

    // @Schema(example = "firstname", description = "Your firstname")
    private String firstname;

    // @Schema(example = "lastname", description = "Your lastName")
    private String lastname;

    // @Schema(example = "profilePicture", description = "upload your profile picture")
    private String profilePicture;
}
