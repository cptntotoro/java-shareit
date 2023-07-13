package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithRequestId;
import ru.practicum.shareit.request.dto.RequestDtoOutput;
import ru.practicum.shareit.request.dto.RequestDtoInput;
import ru.practicum.shareit.request.dto.RequestDtoShortOutput;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {"db.name=test"})
public class RequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestService requestService;

    @Autowired
    private MockMvc mockMvc;

    private RequestDtoInput requestDtoInput;
    private RequestDtoShortOutput requestDtoShortOutput;
    private RequestDtoOutput requestDtoOutput;

    @BeforeEach
    void setup() {
        ItemDto itemDto = new ItemDto(1, "Balalaika", "Brand new balalaika", true);
        ItemDtoWithRequestId itemDtoWithRequestId = new ItemDtoWithRequestId(itemDto, 1);

        User user = new User(2, "Shaun", "shaun@ya.ru");

        Request request = new Request(1, "Looking for a balalaika", LocalDateTime.now(), user);

        requestDtoShortOutput = new RequestDtoShortOutput(1, "Looking for a balalaika", LocalDateTime.now());
        requestDtoOutput = new RequestDtoOutput(request, List.of(itemDtoWithRequestId));
        requestDtoInput = new RequestDtoInput("Looking for a balalaika");
    }

    @Test
    void add_shouldReturnStatusOk() throws Exception {
        when(requestService.add(any(), any())).thenReturn(requestDtoShortOutput);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoShortOutput.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(requestDtoShortOutput.getDescription())));
        verify(requestService, times(1)).add(any(), any());
    }

    @Test
    void getByUser_shouldReturnStatusOk() throws Exception {
        when(requestService.getByUser(any())).thenReturn(List.of(requestDtoOutput));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(requestDtoOutput.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].description", is(requestDtoOutput.getDescription())));
        verify(requestService, times(1)).getByUser(any());
    }

    @Test
    void getAll_shouldReturnStatusOk() throws Exception {
        when(requestService.getAll(any(), any(), any())).thenReturn(List.of(requestDtoOutput));

        mockMvc.perform(get("/requests/all")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(requestDtoOutput.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].description", is(requestDtoOutput.getDescription())));
        verify(requestService, times(1)).getAll(any(), any(), any());
    }

    @Test
    void get_shouldReturnStatusOk() throws Exception {
        when(requestService.get(any(), any())).thenReturn(requestDtoOutput);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoOutput.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(requestDtoOutput.getDescription())));
        verify(requestService, times(1)).get(any(), any());
    }
}