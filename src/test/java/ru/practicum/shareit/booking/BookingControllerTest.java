package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {"db.name=test"})
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    private BookingDtoInput bookingDtoInput;
    private BookingDtoOutput bookingDtoOutput;

    @BeforeEach
    void setup() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        ItemDto itemDto = new ItemDto(1, "Balalaika", "Brand new balalaika", true);
        UserDto userDto = new UserDto(1, "Jason", "jason@ya.ru");

        bookingDtoInput = new BookingDtoInput(2, 1, start, end);
        bookingDtoOutput = new BookingDtoOutput(1, bookingDtoInput.getStart(), bookingDtoInput.getEnd(), itemDto, userDto, BookingStatus.WAITING);
    }

    @Test
    void add_shouldReturnStatusOk() throws Exception {
        when(bookingService.add(any(), any())).thenReturn(bookingDtoOutput);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Integer.class));

        verify(bookingService, times(1)).add(any(), any());
    }

    @Test
    void setApprove_shouldReturnStatusOk() throws Exception {
        when(bookingService.setApprove(any(), any(), any())).thenReturn(bookingDtoOutput);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", String.valueOf(true))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Integer.class));

        verify(bookingService, times(1)).setApprove(any(), any(), any());
    }

    @Test
    void get_shouldReturnStatusOk() throws Exception {
        when(bookingService.get(any(), any())).thenReturn(bookingDtoOutput);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Integer.class));

        verify(bookingService, times(1)).get(any(), any());
    }

    @Test
    void getAll_shouldReturnStatusOk() throws Exception {
        when(bookingService.getAll(any(), any(), any(), any())).thenReturn(List.of(bookingDtoOutput));

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDtoOutput.getId()), Integer.class));

        verify(bookingService, times(1)).getAll(any(), any(), any(), any());
    }

    @Test
    void getAllByOwner_shouldReturnStatusOk() throws Exception {
        when(bookingService.getAllByOwner(any(), any(), any(), any())).thenReturn(List.of(bookingDtoOutput));

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDtoOutput.getId()), Integer.class));
        verify(bookingService, times(1)).getAllByOwner(any(), any(), any(), any());
    }
}
