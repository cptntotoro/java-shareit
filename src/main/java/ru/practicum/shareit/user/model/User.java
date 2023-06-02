package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Positive
    private Integer id;
    private String name;
    @NotNull(message = "User email can't be null.")
    @NotEmpty(message = "User email can't be empty.")
    @Email
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Incorrect email format.")
    private String email;
}
