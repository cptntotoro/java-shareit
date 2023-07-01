package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDtoWithRequestId add(Integer userId, ItemDtoWithRequestId itemDtoWithRequestId) {
        validateItemDto(itemDtoWithRequestId);
        validateUser(userId);

        Item item = itemMapper.toItem(itemDtoWithRequestId);

        if (itemDtoWithRequestId.getRequestId() != null) {
            item.setRequest(requestRepository.findById(itemDtoWithRequestId.getRequestId()).get());
        }

        item.setOwner(userRepository.findById(userId).get());
        item = itemRepository.save(item);

        ItemDtoWithRequestId itemDtoOutput = itemMapper.toItemDtoWithRequestId(item);
        itemDtoOutput.setRequestId(itemDtoWithRequestId.getRequestId());
        return itemDtoOutput;
    }

    @Override
    public ItemDto update(Integer itemId, Integer userId, ItemDto itemDto) {
        validateItem(itemId);

        if (itemId.equals(itemDto.getId())) {
            validateUser(userId);
        }

        Item item = itemRepository.findById(itemId).get();

        if (!Objects.equals(userId, item.getOwner().getId())) {
            throw new ItemAccessException("Failed to update item. Only item owners are allowed to update items.");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        item = itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDtoExtended get(Integer itemId, Integer userId) {
        validateItem(itemId);

        Sort sort = Sort.by("start").descending();

        Item item = itemRepository.findById(itemId).get();
        ItemDto itemDto = itemMapper.toItemDto(item);

        List<CommentOutputDto> itemComments = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::toCommentOutputDto).collect(Collectors.toList());

        ItemDtoExtended itemDtoExtended = new ItemDtoExtended(itemDto, itemComments);

        if (Objects.equals(userId, item.getOwner().getId())) {
            List<Booking> last = bookingRepository.findByItemIdAndStartIsBeforeAndStatusNot(itemId, LocalDateTime.now(), BookingStatus.REJECTED, sort);
            if (!last.isEmpty()) {
                Booking lastBooking = last.get(0);
                itemDtoExtended.setLastBooking(bookingMapper.toBookingDtoShortOutput(lastBooking));
            }

            List<Booking> next = bookingRepository.findByItemIdAndStartIsAfterAndStatusNot(itemId, LocalDateTime.now(), BookingStatus.REJECTED, sort.ascending());
            if (!next.isEmpty()) {
                Booking nextBooking = next.get(0);
                itemDtoExtended.setNextBooking(bookingMapper.toBookingDtoShortOutput(nextBooking));
            }
        }
        return itemDtoExtended;
    }

    @Override
    public List<ItemDtoExtended> getAll(Integer userId) {
        Sort sort = Sort.by("start").descending();

        List<ItemDtoExtended> userItems = itemRepository.findByOwnerIdOrderByIdAsc(userId).stream()
                .map(itemMapper::toItemDto)
                .map(itemDto -> new ItemDtoExtended(itemDto, null))
                .map(item -> {
            List<Booking> last = bookingRepository.findByItemIdAndStartIsBeforeAndStatusNot(item.getId(), LocalDateTime.now(), BookingStatus.REJECTED, sort);
            if (!last.isEmpty()) {
                Booking lastBooking = last.get(0);
                item.setLastBooking(bookingMapper.toBookingDtoShortOutput(lastBooking));
            }

            List<Booking> next = bookingRepository.findByItemIdAndStartIsAfterAndStatusNot(item.getId(), LocalDateTime.now(), BookingStatus.REJECTED, sort.ascending());
            if (!next.isEmpty()) {
                Booking nextBooking = next.get(0);
                item.setNextBooking(bookingMapper.toBookingDtoShortOutput(nextBooking));
            }
            return item;
        }).collect(Collectors.toList());

        return userItems;
    }

    @Override
    public List<ItemDto> search(Integer userId, String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        String searchQuery = text.toLowerCase();
        return itemRepository.findAll().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchQuery)
                        || item.getDescription().toLowerCase().contains(searchQuery))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentOutputDto addComment(Integer itemId, Integer userId, Comment commentInput) {
        validateUser(userId);
        validateItem(itemId);

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if ((optionalItem.isEmpty())) {
            throw new ObjectNotFoundException("Booking with id = " + itemId + " was not found.");
        }
        Item item = optionalItem.get();

        Sort sort = Sort.by("start").descending();

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);

        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), pageable).toList();
        if (bookings.isEmpty()) {
            throw new UnavailableItemBookingException("Failed to add comment. No finished bookings found.");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new IllegalItemBookingException("Failed to add comment. Item owners are not allowed to comment on the booking of their items.");
        }

        commentInput.setItem(item);
        commentInput.setAuthor(userRepository.findById(userId).get());

        Comment comment = commentRepository.save(commentInput);

        return commentMapper.toCommentOutputDto(comment);
    }

    private void validateItemDto(ItemDtoWithRequestId itemDtoWithRequestId) {
        if (itemDtoWithRequestId.getAvailable() == null || itemDtoWithRequestId.getName() == null || itemDtoWithRequestId.getName().isEmpty()
                || itemDtoWithRequestId.getDescription() == null) {
            throw new DtoIntegrityException("Failed to process request. Item's name, description or isAvailable status must not be null.");
        }
        if (itemDtoWithRequestId.getRequestId() != null) {
            if (!requestRepository.existsById(itemDtoWithRequestId.getRequestId())) {
                throw new ObjectNotFoundException("Failed to process request. Request with id = "
                        + itemDtoWithRequestId.getRequestId() + " doesn't exist.");
            }
        }
    }

    private void validateUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Failed to process request. User with id = " + userId + " doesn't exist.");
        }
    }

    private void validateItem(Integer itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ObjectNotFoundException("Failed to process request. Item with id = " + itemId + " doesn't exist.");
        }
    }
}
