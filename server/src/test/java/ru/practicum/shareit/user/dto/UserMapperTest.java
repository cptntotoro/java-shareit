package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserMapperTest {

    @Test
    void toUser_FromUserDto() {
        UserDto userDto = new UserDto(1, "Jason", "jason@ya.ru");
        User user = UserMapper.INSTANCE.toUser(userDto);

        assertNotNull(user);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void toUserDto_FromUser() {
        User user = new User(1, "Jason", "jason@ya.ru");
        UserDto userDto = UserMapper.INSTANCE.toUserDto(user);

        assertNotNull(userDto);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }
}