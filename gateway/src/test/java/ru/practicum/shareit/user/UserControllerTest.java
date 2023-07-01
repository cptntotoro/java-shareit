package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Autowired
    private MockMvc mockMvc;

    UserDto userDto;

    @BeforeEach
    void setup() {
        userDto = new UserDto(1, "Jason", "jason@ya.ru");
    }

    @Test
    void add_shouldReturnStatusOk() throws Exception {
        String userJson = objectMapper.writeValueAsString(userDto);
        ResponseEntity<Object> response = new ResponseEntity<>(userJson, HttpStatus.OK);

        when(userClient.add(any())).thenReturn(response);

        String content = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(userJson, content);

        verify(userClient).add(any());
    }

    @Test
    void update_shouldReturnStatusOk() throws Exception {
        String userJson = objectMapper.writeValueAsString(userDto);
        ResponseEntity<Object> response = new ResponseEntity<>(userJson, HttpStatus.OK);

        when(userClient.update(any(), anyInt())).thenReturn(response);

        String content = mockMvc.perform(patch("/users/{id}", 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(userJson, content);

        verify(userClient).update(any(), anyInt());
    }

    @Test
    void get_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk());

        verify(userClient).get(anyInt());
    }

    @Test
    void delete_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());

        verify(userClient).delete(anyInt());
    }

    @Test
    void getAll_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient).getAll();
    }
}