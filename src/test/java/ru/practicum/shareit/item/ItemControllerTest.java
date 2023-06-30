package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExtended;
import ru.practicum.shareit.item.dto.ItemDtoWithRequestId;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {"db.name=test"})
public class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    private ItemDtoWithRequestId itemDtoWithRequestId;
    private ItemDtoExtended itemDtoExtended;
    private ItemDto itemDto;
    private CommentOutputDto commentDto;

    @BeforeEach
    void setup() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        itemDto = new ItemDto(1, "Balalaika", "Brand new balalaika", true);

        itemDtoWithRequestId = new ItemDtoWithRequestId(itemDto, 1);
        itemDtoExtended = new ItemDtoExtended(itemDto, null);

        commentDto = new CommentOutputDto(1,"Great balalaika! Thanks!", "Shaun", LocalDateTime.now().minusDays(3));
    }

    @Test
    void add() throws Exception {
        when(itemService.add(any(), any())).thenReturn(itemDtoWithRequestId);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoWithRequestId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithRequestId.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithRequestId.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithRequestId.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithRequestId.getAvailable())));

        verify(itemService, times(1)).add(any(), any());
    }

    @Test
    void update() throws Exception {
        when(itemService.update(any(), any(), any())).thenReturn(itemDtoWithRequestId);

        mockMvc.perform(patch("/items/{Id}", 1)
                        .content(mapper.writeValueAsString(itemDtoWithRequestId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithRequestId.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithRequestId.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithRequestId.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithRequestId.getAvailable())));

        verify(itemService, times(1)).update(any(), any(), any());
    }

    @Test
    void get_shouldReturnStatusOk() throws Exception {
        when(itemService.get(any(), any())).thenReturn(itemDtoWithRequestId);

        mockMvc.perform(get("/items/{Id}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithRequestId.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithRequestId.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithRequestId.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithRequestId.getAvailable())));

        verify(itemService, times(1)).get(any(), any());
    }

    @Test
    void getAll() throws Exception {
        when(itemService.getAll(any())).thenReturn(List.of(itemDtoExtended));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDtoExtended.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(itemDtoExtended.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDtoExtended.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDtoExtended.getAvailable())));

        verify(itemService, times(1)).getAll(any());
    }

    @Test
    void search() throws Exception {
        when(itemService.search(any(), any())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "balalaika"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).search(any(), any());
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(any(), any(), any())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{id}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));

        verify(itemService, times(1)).addComment(any(), any(), any());
    }
}
