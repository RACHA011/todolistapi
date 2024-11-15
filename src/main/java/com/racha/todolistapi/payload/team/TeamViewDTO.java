package com.racha.todolistapi.payload.team;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamViewDTO {

    private long id;

    private List<Long> friends;
}
