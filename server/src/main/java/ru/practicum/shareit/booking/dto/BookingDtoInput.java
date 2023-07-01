package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoInput {
    private Integer bookerId;
    private Integer itemId;

    @FutureOrPresent
    @NotNull
    private LocalDateTime start;

    @FutureOrPresent
    @NotNull
    private LocalDateTime end;
}