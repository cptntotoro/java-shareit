package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(UserDto userDto);

    UserDto update(int id, UserDto userDto);

    UserDto get(int id);

    void delete(int id);

    List<UserDto> getAll();
}