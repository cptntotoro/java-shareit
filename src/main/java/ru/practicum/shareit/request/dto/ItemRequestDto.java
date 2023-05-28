package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private int id;
    private String description;
    private UserDto requestingUser;
    private LocalDateTime created;
}

