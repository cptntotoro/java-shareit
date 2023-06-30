package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.UserEmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.DtoIntegrityException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    private final UserRepository userRepository;

    public UserDto add(@Valid UserDto userDto) {
        validateUserDto(userDto);
        User user = userMapper.toUser(userDto);
        user = userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    public UserDto update(int userId, @Valid UserDto userDto) {
        validateUserById(userId);

        User user = userRepository.findById(userId).get();

        if (userDto.getEmail() != null) {
            if (!Objects.equals(userDto.getEmail(), user.getEmail()) && userWithEmailExists(userDto.getEmail())) {
                throw new UserEmailAlreadyExistsException("Failed to update user. User with email " + userDto.getEmail() + " already exists.");
            }
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        user = userRepository.save(user);

        return userMapper.toUserDto(user);
    }

    public UserDto get(int id) {
        validateUserById(id);
        Optional<User> user = userRepository.findById(id);
        return user.map(userMapper::toUserDto).orElse(null);
    }

    public void delete(int id) {
        validateUserById(id);
        userRepository.deleteById(id);
    }

    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    private void validateUserDto(UserDto userDto) {
        if (userDto.getName() == null || userDto.getEmail() == null) {
            throw new DtoIntegrityException("Failed to process request. Item's name, description or isAvailable status must not be null.");
        }
    }

    private void validateUserById(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Failed to process request. User with id = " + userId + " doesn't exist.");
        }
    }

    private boolean userWithEmailExists(String email) {
        return userRepository.findAll().stream().map(User::getEmail).anyMatch(email::equals);
    }
}
