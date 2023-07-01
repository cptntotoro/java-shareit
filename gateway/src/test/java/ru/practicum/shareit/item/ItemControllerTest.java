package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithRequestId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mockMvc;

    private ItemDtoWithRequestId itemDtoWithRequestId;
    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setup() {
        itemDto = new ItemDto(1, "Balalaika", "Brand new balalaika", true);
        itemDtoWithRequestId = new ItemDtoWithRequestId(itemDto, 1);
        commentDto = new CommentDto(1, "Great balalaika! Thanks!", itemDto, "Shaun");
    }

    @Test
    void add_shouldReturnStatusOk() throws Exception {
        String itemJson = objectMapper.writeValueAsString(itemDto);
        ResponseEntity<Object> response = new ResponseEntity<>(itemJson, HttpStatus.OK);

        when(itemClient.add(any(), anyInt())).thenReturn(response);

        String content = mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(itemJson, content);

        verify(itemClient).add(any(), anyInt());
    }

    @Test
    void update_shouldReturnStatusOk() throws Exception {
        String itemJson = objectMapper.writeValueAsString(itemDto);
        ResponseEntity<Object> response = new ResponseEntity<>(itemJson, HttpStatus.OK);

        when(itemClient.update(any(), anyInt(), anyInt())).thenReturn(response);

        String content = mockMvc.perform(patch("/items/{Id}", 1)
                        .content(objectMapper.writeValueAsString(itemDtoWithRequestId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(itemJson, content);

        verify(itemClient).update(any(), anyInt(), anyInt());
    }

    @Test
    void get_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(get("/items/{Id}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemClient).get(anyInt(), anyInt());
    }

    @Test
    void getAll_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemClient).getAll(any());
    }

    @Test
    void search_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "balalaika"))
                .andExpect(status().isOk());

        verify(itemClient).search(anyInt(), any());
    }

    @Test
    void addComment() throws Exception {
        String commentJson = objectMapper.writeValueAsString(commentDto);
        ResponseEntity<Object> response = new ResponseEntity<>(commentJson, HttpStatus.OK);

        when(itemClient.addComment(anyInt(), anyInt(), any())).thenReturn(response);

        String content = mockMvc.perform(post("/items/{id}/comment", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(commentJson, content);

        verify(itemClient).addComment(anyInt(), anyInt(), any());
    }
}