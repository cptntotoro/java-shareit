package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDtoInput;
import ru.practicum.shareit.request.dto.RequestDtoOutput;
import ru.practicum.shareit.request.dto.RequestDtoShortOutput;

import java.util.List;

public interface RequestService {
    RequestDtoShortOutput add(RequestDtoInput itemRequestDtoOutput, Integer userId);

    List<RequestDtoOutput> getByUser(Integer userId);

    List<RequestDtoOutput> getAll(Integer from, Integer size, Integer userId);

    RequestDtoOutput get(Integer requestId, Integer userId);
}
