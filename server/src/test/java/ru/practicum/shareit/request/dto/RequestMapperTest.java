package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RequestMapperTest {

    @Test
    void toRequestDtoShortOutput_FromRequest() {
        User user = new User(2, "Shaun", "shaun@ya.ru");
        Request request = new Request(1, "Looking for Balalaika", LocalDateTime.now(), user);

        RequestDtoShortOutput requestDtoShortOutput = RequestMapper.INSTANCE.toRequestDtoShortOutput(request);

        assertNotNull(requestDtoShortOutput);

        assertEquals(request.getId(), requestDtoShortOutput.getId());
        assertEquals(request.getDescription(), requestDtoShortOutput.getDescription());
        assertEquals(request.getCreated(), requestDtoShortOutput.getCreated());
    }

    @Test
    void toRequestDtoOutput_FromRequest() {
        User user = new User(2, "Shaun", "shaun@ya.ru");
        Request request = new Request(1, "Looking for Balalaika", LocalDateTime.now(), user);

        RequestDtoOutput requestDtoOutput = RequestMapper.INSTANCE.toRequestDtoOutput(request);

        assertNotNull(requestDtoOutput);

        assertEquals(request.getId(), requestDtoOutput.getId());
        assertEquals(request.getDescription(), requestDtoOutput.getDescription());
        assertEquals(request.getCreated(), requestDtoOutput.getCreated());
        assertNull(requestDtoOutput.getItems());
    }

    @Test
    void toRequest_FromRequestDtoInput() {
        RequestDtoInput requestDtoInput = new RequestDtoInput("Looking for Balalaika");
        Request request = RequestMapper.INSTANCE.toRequest(requestDtoInput);

        assertNotNull(request);

        assertNull(request.getId());
        assertEquals(requestDtoInput.getDescription(), request.getDescription());
        assertNull(request.getRequestingUser());
        assertNotNull(request.getCreated());
    }
}