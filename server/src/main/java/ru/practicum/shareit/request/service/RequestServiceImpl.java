package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DtoIntegrityException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoWithRequestId;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDtoInput;
import ru.practicum.shareit.request.dto.RequestDtoOutput;
import ru.practicum.shareit.request.dto.RequestDtoShortOutput;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    public RequestDtoShortOutput add(RequestDtoInput requestDtoInput, Integer userId) {
        validateUser(userId);
        validateItemRequestDtoInput(requestDtoInput);

        Request request = requestMapper.toRequest(requestDtoInput);

        request.setRequestingUser(userRepository.findById(userId).get());

        request = requestRepository.save(request);

        return requestMapper.toRequestDtoShortOutput(request);
    }

    @Override
    public List<RequestDtoOutput> getByUser(Integer userId) {
        validateUser(userId);
        List<Request> requests = requestRepository.findByRequestingUserIdOrderByCreatedDesc(userId);
        List<RequestDtoOutput> itemRequestOutput = requests.stream()
                .map(requestMapper::toRequestDtoOutput)
                .collect(Collectors.toList());

        itemRequestOutput.forEach(itemRequest -> itemRequest.setItems(findItemsByRequestId(itemRequest.getId())));
        return itemRequestOutput;
    }

    @Override
    public List<RequestDtoOutput> getAll(Integer from, Integer size, Integer userId) {
        validateUser(userId);

        List<Request> requests = requestRepository.findByRequestingUserIdNotOrderByCreatedDesc(userId);
        List<RequestDtoOutput> itemRequestOutput = requests.stream()
                .map(requestMapper::toRequestDtoOutput)
                .collect(Collectors.toList());

        itemRequestOutput.forEach(itemRequest -> itemRequest.setItems(findItemsByRequestId(itemRequest.getId())));

        return itemRequestOutput;
    }

    @Override
    public RequestDtoOutput get(Integer requestId, Integer userId) {
        validateUser(userId);
        validateRequest(requestId);

        Request request = requestRepository.findById(requestId).get();
        RequestDtoOutput itemRequestOutput = requestMapper.toRequestDtoOutput(request);

        itemRequestOutput.setItems(findItemsByRequestId(requestId));

        return itemRequestOutput;
    }

    private List<ItemDtoWithRequestId> findItemsByRequestId(Integer requestId) {
        List<Item> items = itemRepository.findByRequestId(requestId);
        return items.stream()
                .map(itemMapper::toItemDto)
                .map(itemDto -> new ItemDtoWithRequestId(itemDto, requestId))
                .collect(Collectors.toList());
    }

    private void validateItemRequestDtoInput(RequestDtoInput requestDtoInput) {
        if (requestDtoInput.getDescription() == null || requestDtoInput.getDescription().isEmpty()) {
            throw new DtoIntegrityException("Failed to process request. Item request description must not be null or empty.");
        }
    }

    private void validateUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Failed to process request. User with id = " + userId + " doesn't exist.");
        }
    }

    private void validateRequest(Integer requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new ObjectNotFoundException("Failed to process request. User with id = " + requestId + " doesn't exist.");
        }
    }
}
