package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Booking {
    private int id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Item item;
    private User booker;
    private BookingStatus status; // Владелец вещи должен подтвердить бронирование
    // После того как вещь возвращена, у пользователя, который её арендовал, должна быть возможность оставить отзыв.
    //  В отзыве можно поблагодарить владельца вещи и подтвердить, что задача выполнена.
}
