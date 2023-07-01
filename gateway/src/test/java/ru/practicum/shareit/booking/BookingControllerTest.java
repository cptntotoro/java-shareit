package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSearchMode;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mockMvc;

    private BookingDto bookingDto;

    @BeforeEach
    void setup() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        bookingDto = new BookingDto(1, start, end);
    }

    @Test
    void add_shouldReturnStatusOk() throws Exception {
        String bookingJson = objectMapper.writeValueAsString(bookingDto);
        ResponseEntity<Object> response = new ResponseEntity<>(bookingJson, HttpStatus.OK);

        when(bookingClient.add(anyInt(), any())).thenReturn(response);

        String content = mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(bookingJson, content);

        verify(bookingClient).add(anyInt(), any());
    }

    @Test
    void setApprove_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, true)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingClient).setApprove(1, 1, true);
    }

    @Test
    void get_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingClient).get(1, 1);
    }

    @Test
    void getAll_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(get("/bookings/?state={state}&from={from}&size={size}", BookingSearchMode.ALL, 1, 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingClient).getAll(1, BookingSearchMode.ALL, 1, 1);
    }

    @Test
    void getAllByOwner_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", BookingSearchMode.ALL, 1, 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingClient).getAllByOwner(1, BookingSearchMode.ALL, 1, 1);
    }
}