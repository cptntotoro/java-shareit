package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDtoWithRequestId;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@Data
@NoArgsConstructor
public class RequestDtoOutput extends RequestDtoShortOutput {
    @Getter
    private List<ItemDtoWithRequestId> items;

    public RequestDtoOutput(Request request, List<ItemDtoWithRequestId> items) {
        super(request.getId(), request.getDescription(), request.getCreated());
        this.items = items;
    }
}

