package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Positive
    private Integer id;
    private String name;
    private String description;
    private Boolean isAvailable;
    private User owner;
    private ItemRequest request;

    public Item(Integer id, String name, String description, Boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
    }
}
