package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.RequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
public class RequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    @Autowired
    private MockMvc mockMvc;

    private RequestDto requestDto;

    @BeforeEach
    void setup() {
        requestDto = new RequestDto("Looking for a balalaika");
    }

    @Test
    void add_shouldReturnStatusOk() throws Exception {
        String requestJson = objectMapper.writeValueAsString(requestDto);
        ResponseEntity<Object> response = new ResponseEntity<>(requestJson, HttpStatus.OK);

        when(requestClient.add(anyInt(), any())).thenReturn(response);

        String content = mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(requestJson, content);

        verify(requestClient).add(anyInt(), any());
    }

    @Test
    void getByUser_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(requestClient).getByUser(anyInt());
    }

    @Test
    void getAll_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(requestClient).getAll(anyInt(), anyInt(), anyInt());
    }

    @Test
    void get_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(requestClient).get(anyInt(), anyInt());
    }
}