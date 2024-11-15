package com.racha.todolistapi.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileViewDTO {

    private Long id;

    private String username;

    private String firstname;

    private String lastname;

    private String profilePicture;
}
