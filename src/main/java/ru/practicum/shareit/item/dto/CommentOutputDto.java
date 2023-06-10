package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentOutputDto {
    private Integer id;
    private String text;
    private String authorName;
    LocalDateTime created = LocalDateTime.now();
}
