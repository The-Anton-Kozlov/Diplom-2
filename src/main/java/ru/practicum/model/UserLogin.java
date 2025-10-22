package ru.practicum.model;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class UserLogin {

    private String email;
    private String password;
}
