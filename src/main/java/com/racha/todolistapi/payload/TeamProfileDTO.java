package com.racha.todolistapi.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamProfileDTO {
    private Long id;

    private String username;

    private String profilePicture;
}
