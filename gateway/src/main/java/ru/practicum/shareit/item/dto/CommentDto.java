package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Integer id;

    @NotNull(message = "Comment text must not be null.")
    @NotBlank(message = "Comment must not be empty.")
    private String text;

    private ItemDto item;

    private String authorName;
}
