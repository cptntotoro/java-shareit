package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exceptions.DtoIntegrityException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.UserEmailAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper;

    private User user1;

    private User user2;

    @BeforeEach
    void setup() {
        user1 = new User(1, "Jason", "jason@ya.ru");
        user2 = new User(2, "Shaun", "shaun@ya.ru");
    }

    @Test
    void add_shouldSaveAndReturnCorrectUserDto() {
        UserDto userDtoToSave = userMapper.toUserDto(user1);

        Mockito.verify(userMapper).toUserDto(user1);

        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user1);

        UserDto userDtoSaved = userService.add(userDtoToSave);
        Mockito.verify(userRepository).save(any());

        assertEquals(user1.getId(), userDtoSaved.getId());

        verify(userRepository, atMostOnce()).saveAndFlush(any());
    }

    @Test
    void add_shouldThrowDtoIntegrityException_WhenUserDtoIsEmpty() {
        assertThrows(DtoIntegrityException.class, () -> userService.add(new UserDto()));
    }

    @Test
    void add_shouldThrowDtoIntegrityException_WhenUserDtoNameIsEmpty() {
        User userToSave = new User();
        userToSave.setEmail(user1.getEmail());

        UserDto userDtoToSave = userMapper.toUserDto(userToSave);
        Mockito.verify(userMapper).toUserDto(userToSave);

        assertThrows(DtoIntegrityException.class, () -> userService.add(userDtoToSave));
    }

    @Test
    void add_shouldThrowDtoIntegrityException_WhenUserDtoEmailIsEmpty() {
        User userToSave = new User();
        userToSave.setName(user1.getName());

        UserDto userDtoToSave = userMapper.toUserDto(userToSave);
        Mockito.verify(userMapper).toUserDto(userToSave);

        assertThrows(DtoIntegrityException.class, () -> userService.add(userDtoToSave));
    }

    @Test
    void update_shouldReturnUserDtoWithNewNameAndNewEmail() {
        UserDto userDtoToUpdateTo = userMapper.toUserDto(user2);
        Mockito.verify(userMapper).toUserDto(user2);

        Integer userId = user2.getId();

        Mockito.when(userRepository.existsById(userDtoToUpdateTo.getId()))
                .thenReturn(true);

        Mockito.when(userRepository.findById(userDtoToUpdateTo.getId()))
                .thenReturn(Optional.of(user1));

        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user2);

        UserDto updatedUserDto = userService.update(userId, userDtoToUpdateTo);

        assertAll(
                () -> assertEquals(user2.getId(), updatedUserDto.getId()),
                () -> assertEquals(user2.getName(), updatedUserDto.getName()),
                () -> assertEquals(user2.getEmail(), updatedUserDto.getEmail())
        );

        verify(userRepository).save(any());
    }

    @Test
    void update_shouldThrowObjectNotFoundException_whenUserNotFound() {
        UserDto userDtoToUpdateTo = userMapper.toUserDto(user2);
        Mockito.verify(userMapper).toUserDto(user2);

        Integer userId = user2.getId();

        Mockito.when(userRepository.existsById(userDtoToUpdateTo.getId()))
                .thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> userService.update(userId, userDtoToUpdateTo));
    }

    @Test
    void update_shouldThrowUserEmailAlreadyExistsException() {
        UserDto userDtoToUpdateTo = userMapper.toUserDto(user2);

        Mockito.verify(userMapper).toUserDto(user2);

        Integer userId = user1.getId();

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user1));

        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        assertThrows(UserEmailAlreadyExistsException.class, () -> userService.update(userId, userDtoToUpdateTo));
    }

    @Test
    void get_shouldReturnUserDto() {
        Integer userId = user1.getId();

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user1));

        UserDto actualUser = userService.get(userId);

        assertEquals(user1.getId(), actualUser.getId());

        verify(userRepository).findById(userId);
    }

    @Test
    void delete_shouldInvokeUserRepositoryExistsById() {
        Integer userId = user1.getId();

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        userService.delete(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void delete_shouldThrowObjectNotFoundException_whenUserNotFound() {
        Integer userId = user1.getId();

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> userService.delete(userId));
    }

    @Test
    void getAll_shouldReturnListOfUserDto() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        List<UserDto> actualUsers = userService.getAll();

        assertAll(
                () -> assertEquals(2, actualUsers.size()),
                () -> assertEquals(user1.getId(), actualUsers.get(0).getId()),
                () -> assertEquals(user2.getId(), actualUsers.get(1).getId())
        );

        verify(userRepository).findAll();
    }

    @Test
    void getAll_shouldReturnEmptyListOfUserDto_whenUsersNotFound() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of());

        List<UserDto> actualUsers = userService.getAll();

        assertTrue(actualUsers.isEmpty());

        verify(userRepository).findAll();
    }

    @Test
    void incorrectUserDtoEmailCausesConstraintViolation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        User userToSave = new User();
        userToSave.setName(user1.getName());
        userToSave.setEmail("user");

        Set<ConstraintViolation<User>> violations = validator.validate(userToSave);

        assertFalse(violations.isEmpty());
    }
}