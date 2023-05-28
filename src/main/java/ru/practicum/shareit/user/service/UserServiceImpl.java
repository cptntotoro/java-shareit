package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserDoesNotExistException;
import ru.practicum.shareit.exceptions.UserEmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserDtoIntegrityException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private Map<Integer, User> users = new HashMap<>();
    private int idGenerator = 0;
    private final UserMapper userMapper;

    public UserDto add(@Valid UserDto userDto) {
        validateUserDto(userDto);
        if (userWithEmailExists(userDto.getEmail())) {
            throw new UserEmailAlreadyExistsException("Failed to add user. User with email " + userDto.getEmail() + " already exists.");
        }
        User user = userMapper.toUser(userDto);
        user.setId(++idGenerator);
        users.put(user.getId(), user);
        return userMapper.toUserDto(user);
    }

    public UserDto update(int userId, @Valid UserDto userDto) {
        validateUserById(userId);

        if (userDto.getEmail() != null) {
            if (!Objects.equals(userDto.getEmail(), users.get(userId).getEmail()) && userWithEmailExists(userDto.getEmail())) {
                throw new UserEmailAlreadyExistsException("Failed to update user. User with email " + userDto.getEmail() + " doesn't exist.");
            }
            users.get(userId).setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            users.get(userId).setName(userDto.getName());
        }
        return userMapper.toUserDto((users.get(userId)));
    }

    public UserDto get(int id) {
        validateUserById(id);
        return userMapper.toUserDto((users.get(id)));
    }

    public void delete(int id) {
        validateUserById(id);
        users.remove(id);
    }

    public List<UserDto> getAll() {
        return users.values().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    private void validateUserDto(UserDto userDto) {
        if (userDto.getName() == null || userDto.getEmail() == null) {
            throw new UserDtoIntegrityException("Failed to process request. Item's name, description or isAvailable status must not be null.");
        }
    }

    private void validateUserById(Integer userId) {
        if (!users.containsKey(userId)) {
            throw new UserDoesNotExistException("Failed to process request. User with id = " + userId + " doesn't exist.");
        }
    }

    private boolean userWithEmailExists(String email) {
        return users.values().stream().map(User::getEmail).anyMatch(email::equals);
    }
}
