package com.racha.todolistapi.payload.team;

import java.util.List;

import com.racha.todolistapi.model.Profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamDTO {
    private List<Profile> team;
}
