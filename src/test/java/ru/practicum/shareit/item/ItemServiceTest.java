package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.DtoIntegrityException;
import ru.practicum.shareit.exceptions.ItemAccessException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.UnavailableItemBookingException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
public class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RequestRepository requestRepository;

    @Spy
    private Item item;

    @Spy
    private User user;

    @Spy
    private ItemMapperImpl itemMapper;

    @Spy
    private BookingMapperImpl bookingMapper;

    @Spy
    private CommentMapperImpl commentMapper;

    private Item item1;

    private Item item2;

    private User user1;

    private User user2;

    private Comment comment;

    private Booking lastBooking;

    private Booking nextBooking;

    private Request request;

    @BeforeEach
    void setup() {
        user2 = new User(2, "Shaun", "shaun@ya.ru");
        request = new Request(1, "Looking for a balalaika", LocalDateTime.now(), user2);
        user1 = new User(1, "Jason", "jason@ya.ru");
        item1 = new Item(1, "Balalaika", "Brand new balalaika", true, user1, request);
        item2 = new Item(2, "Matryoshka", "A set of 5 dolls", true);

        lastBooking = new Booking(1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item1, user2, BookingStatus.APPROVED);
        nextBooking = new Booking(2, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item1, user2, BookingStatus.APPROVED);
        comment = new Comment(1,"Great balalaika! Thanks!", item1, user2, LocalDateTime.now().minusDays(2));
    }

    @Test
    void add_shouldReturnItemDto_whenWithoutRequestId() {
        ItemDtoWithRequestId itemDto = itemMapper.toItemDtoWithRequestId(item1);

        Integer userId = user1.getId();

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user1));

        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(item1);

        ItemDtoWithRequestId savedItemDto = itemService.add(userId, itemDto);

        assertAll(
                () -> assertEquals(item1.getId(), savedItemDto.getId()),
                () -> assertEquals(item1.getName(), savedItemDto.getName()),
                () -> assertEquals(item1.getDescription(), savedItemDto.getDescription()),
                () -> assertEquals(item1.getAvailable(), savedItemDto.getAvailable()),
                () -> assertNull(savedItemDto.getRequestId())
        );

        verify(itemRepository, atMostOnce()).saveAndFlush(any());
    }

    @Test
    void add_shouldReturnItemDto_whenWithRequestId() {
        ItemDtoWithRequestId itemDtoToSave = itemMapper.toItemDtoWithRequestId(item1);
        itemDtoToSave.setRequestId(request.getId());

        Mockito.when(requestRepository.existsById(itemDtoToSave.getRequestId()))
                .thenReturn(true);

        Mockito.when(requestRepository.findById(request.getId()))
                .thenReturn(Optional.ofNullable(request));

        Integer userId = user1.getId();

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user1));

        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(item1);

        ItemDtoWithRequestId savedItemDto = itemService.add(userId, itemDtoToSave);

        assertAll(
                () -> assertEquals(item1.getId(), savedItemDto.getId()),
                () -> assertEquals(item1.getName(), savedItemDto.getName()),
                () -> assertEquals(item1.getDescription(), savedItemDto.getDescription()),
                () -> assertEquals(item1.getAvailable(), savedItemDto.getAvailable()),
                () -> assertEquals(itemDtoToSave.getRequestId(), savedItemDto.getRequestId())
        );

        verify(itemRepository, atMostOnce()).saveAndFlush(any());
    }

    @Test
    void add_throwsDtoIntegrityException_whenItemDtoWithRequestIdIsEmpty() {
        assertThrows(DtoIntegrityException.class, () -> itemService.add(user1.getId(), new ItemDtoWithRequestId()));
    }

    @Test
    void add_throwsObjectNotFoundException_whenRequestNotFound() {
        ItemDtoWithRequestId itemDtoToSave = itemMapper.toItemDtoWithRequestId(item1);
        itemDtoToSave.setRequestId(request.getId());

        Mockito.when(requestRepository.existsById(request.getId()))
                .thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> itemService.add(user1.getId(), itemDtoToSave));
    }

    @Test
    void update_shouldReturnItemDtoWithNewNameNewDescriptionAndNewEmail() {
        Integer itemIdToUpdate = item1.getId();
        ItemDto itemDtoUpdate = itemMapper.toItemDto(item2);
        Integer userId = user1.getId();

        Mockito.when(itemRepository.existsById(itemIdToUpdate))
                .thenReturn(true);

        item1.setOwner(user1);

        Mockito.when(itemRepository.findById(itemIdToUpdate))
                .thenReturn(Optional.ofNullable(item1));

        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(item1);

        ItemDto savedItemDto = itemService.update(itemIdToUpdate, userId, itemDtoUpdate);

        assertAll(
                () -> assertEquals(item2.getName(), savedItemDto.getName()),
                () -> assertEquals(item2.getDescription(), savedItemDto.getDescription()),
                () -> assertEquals(item2.getAvailable(), savedItemDto.getAvailable())
        );

        verify(itemRepository, atMostOnce()).saveAndFlush(any());
    }

    @Test
    void update_throwsObjectNotFoundException_whenItemNotFound() {
        Integer itemIdToUpdate = item1.getId();
        ItemDto itemDtoUpdate = itemMapper.toItemDto(item2);
        Integer userId = user1.getId();

        Mockito.when(itemRepository.existsById(itemIdToUpdate))
                .thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> itemService.update(itemIdToUpdate, userId, itemDtoUpdate));
    }

    @Test
    void update_throwsObjectNotFoundException_whenUserNotFound() {
        Integer itemIdToUpdate = item1.getId();
        ItemDto itemDtoUpdate = itemMapper.toItemDto(item1);
        Integer userId = user1.getId();

        Mockito.when(itemRepository.existsById(itemIdToUpdate))
                .thenReturn(true);

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> itemService.update(itemIdToUpdate, userId, itemDtoUpdate));
    }

    @Test
    void update_throwsItemAccessException_whenAccessedNotByOwner() {
        Integer itemIdToUpdate = item1.getId();
        ItemDto itemDtoUpdate = itemMapper.toItemDto(item2);
        Integer userId = user2.getId();

        Mockito.when(itemRepository.existsById(itemIdToUpdate))
                .thenReturn(true);

        item1.setOwner(user1);

        Mockito.when(itemRepository.findById(itemIdToUpdate))
                .thenReturn(Optional.ofNullable(item1));

        assertThrows(ItemAccessException.class, () -> itemService.update(itemIdToUpdate, userId, itemDtoUpdate));
    }

    @Test
    void get_shouldReturnItemDtoExtended_whenRequestedByBooker() {
        item1 = new Item(1, "Balalaika", "Brand new balalaika", true, user1, request);
        Integer itemIdToGet = item1.getId();
        Integer userIdRequesting = user1.getId();

        Mockito.when(itemRepository.existsById(itemIdToGet))
                .thenReturn(true);

        Mockito.when(itemRepository.findById(itemIdToGet))
                .thenReturn(Optional.ofNullable(item1));

        Mockito.when(commentRepository.findByItemId(itemIdToGet))
                .thenReturn(List.of());

        Mockito.lenient().when(bookingRepository.findByItemIdAndStartIsBeforeAndStatusNot(itemIdToGet, LocalDateTime.now(), BookingStatus.REJECTED, Sort.by("start").descending()))
                .thenReturn(List.of());

        Mockito.lenient().when(bookingRepository.findByItemIdAndStartIsAfterAndStatusNot(itemIdToGet, LocalDateTime.now(), BookingStatus.REJECTED, Sort.by("start").ascending()))
                .thenReturn(List.of());

        ItemDtoExtended savedItemDtoExtended = itemService.get(itemIdToGet, userIdRequesting);

        assertAll(
                () -> assertEquals(item1.getId(), savedItemDtoExtended.getId()),
                () -> assertEquals(item1.getName(), savedItemDtoExtended.getName()),
                () -> assertEquals(item1.getDescription(), savedItemDtoExtended.getDescription()),
                () -> assertEquals(item1.getAvailable(), savedItemDtoExtended.getAvailable()),
                () -> assertTrue(savedItemDtoExtended.getComments().isEmpty()),
                () -> assertNull(savedItemDtoExtended.getLastBooking()),
                () -> assertNull(savedItemDtoExtended.getNextBooking())
        );

        verify(itemRepository, atMostOnce()).saveAndFlush(any());
    }

    @Test
    void get_shouldReturnItemDtoExtended_whenRequestedNotByBooker() {
        Integer itemId = item1.getId();
        Integer userId = user1.getId();

        Mockito.when(itemRepository.existsById(itemId))
                .thenReturn(true);

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item1));

        Mockito.when(commentRepository.findByItemId(itemId))
                .thenReturn(List.of(comment));

        Mockito.lenient().when(bookingRepository.findByItemIdAndStartIsBeforeAndStatusNot(itemId, LocalDateTime.now(), BookingStatus.REJECTED, Sort.by("start").descending()))
                .thenReturn(List.of());

        Mockito.lenient().when(bookingRepository.findByItemIdAndStartIsAfterAndStatusNot(itemId, LocalDateTime.now(), BookingStatus.REJECTED, Sort.by("start").ascending()))
                .thenReturn(List.of());

        ItemDtoExtended savedItemDtoExtended = itemService.get(itemId, userId);

        assertAll(
                () -> assertEquals(item1.getId(), savedItemDtoExtended.getId()),
                () -> assertEquals(item1.getName(), savedItemDtoExtended.getName()),
                () -> assertEquals(item1.getDescription(), savedItemDtoExtended.getDescription()),
                () -> assertEquals(item1.getAvailable(), savedItemDtoExtended.getAvailable()),
                () -> assertEquals(savedItemDtoExtended.getComments().size(), 1),
                () -> assertNull(savedItemDtoExtended.getLastBooking()),
                () -> assertNull(savedItemDtoExtended.getNextBooking())
        );

        verify(itemRepository).findById(itemId);
    }

    @Test
    void get_shouldReturnItemDtoExtendedWithLastAndNextBooking() {
        Integer itemId = item1.getId();
        Integer userId = user1.getId();

        Mockito.when(itemRepository.existsById(itemId))
                .thenReturn(true);

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item1));

        Mockito.when(commentRepository.findByItemId(itemId))
                .thenReturn(List.of());

        Mockito.lenient().when(bookingRepository.findByItemIdAndStartIsBeforeAndStatusNot(any(), any(LocalDateTime.class), eq(BookingStatus.REJECTED), any()))
                .thenReturn(List.of(lastBooking));

        Mockito.lenient().when(bookingRepository.findByItemIdAndStartIsAfterAndStatusNot(any(), any(LocalDateTime.class), eq(BookingStatus.REJECTED), any()))
                .thenReturn(List.of(nextBooking));

        ItemDtoExtended itemDtoSaved = itemService.get(itemId, userId);

        assertAll(
                () -> assertEquals(item1.getId(), itemDtoSaved.getId()),
                () -> assertEquals(item1.getName(), itemDtoSaved.getName()),
                () -> assertEquals(item1.getDescription(), itemDtoSaved.getDescription()),
                () -> assertEquals(item1.getAvailable(), itemDtoSaved.getAvailable()),
                () -> assertNotNull(itemDtoSaved.getLastBooking()),
                () -> assertNotNull(itemDtoSaved.getNextBooking()),
                () -> assertTrue(itemDtoSaved.getComments().isEmpty())
        );

        verify(itemRepository).findById(itemId);
    }

    @Test
    void get_shouldReturnItemDtoExtendedWithComments() {
        Integer itemId = item1.getId();
        Integer userId = user1.getId();

        Mockito.when(itemRepository.existsById(itemId))
                .thenReturn(true);

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item1));

        Mockito.when(commentRepository.findByItemId(itemId))
                .thenReturn(List.of(comment));

        Mockito.lenient().when(bookingRepository.findByItemIdAndStartIsBeforeAndStatusNot(any(), any(LocalDateTime.class), eq(BookingStatus.REJECTED), any()))
                .thenReturn(List.of(lastBooking));

        Mockito.lenient().when(bookingRepository.findByItemIdAndStartIsAfterAndStatusNot(any(), any(LocalDateTime.class), eq(BookingStatus.REJECTED), any()))
                .thenReturn(List.of(nextBooking));

        ItemDtoExtended savedItemDtoExtended = itemService.get(itemId, userId);

        assertAll(
                () -> assertEquals(item1.getId(), savedItemDtoExtended.getId()),
                () -> assertEquals(item1.getName(), savedItemDtoExtended.getName()),
                () -> assertEquals(item1.getDescription(), savedItemDtoExtended.getDescription()),
                () -> assertEquals(item1.getAvailable(), savedItemDtoExtended.getAvailable()),
                () -> assertEquals(savedItemDtoExtended.getComments().size(), 1)
        );

        verify(itemRepository).findById(itemId);
    }

    @Test
    void get_throwsObjectNotFoundException_whenItemNotFound() {
        Integer itemIdToGet = item1.getId();
        Integer userIdRequesting = user1.getId();

        Mockito.when(itemRepository.existsById(itemIdToGet))
                .thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> itemService.get(itemIdToGet, userIdRequesting));
    }

    @Test
    void getAll() {
        Integer userId = user1.getId();

        Mockito.when(itemRepository.findByOwnerId(userId))
                .thenReturn(List.of(item1));

        Mockito.lenient().when(bookingRepository.findByItemIdAndStartIsBeforeAndStatusNot(any(), any(LocalDateTime.class), eq(BookingStatus.REJECTED), any()))
                .thenReturn(List.of(lastBooking));

        Mockito.lenient().when(bookingRepository.findByItemIdAndStartIsAfterAndStatusNot(any(), any(LocalDateTime.class), eq(BookingStatus.REJECTED), any()))
                .thenReturn(List.of(nextBooking));

        List<ItemDtoExtended> allUserItems = itemService.getAll(userId);

        assertAll(
                () -> assertFalse(allUserItems.isEmpty()),
                () -> assertNotNull(allUserItems.get(0).getLastBooking()),
                () -> assertNotNull(allUserItems.get(0).getNextBooking())
        );
        verify(itemRepository).findByOwnerId(userId);
    }

    @Test
    void search_shouldReturnListItemDto() {
        Integer userIdRequesting = user1.getId();
        String text = "BaLaLaiKa";

        Mockito.when(itemRepository.findAll())
                .thenReturn(List.of(item1, item2));

        List<ItemDto> itemsFound = itemService.search(userIdRequesting, text);

        assertAll(
                () -> assertFalse(itemsFound.isEmpty()),
                () -> assertEquals(itemsFound.get(0), itemMapper.toItemDto(item1))
        );

        verify(itemRepository).findAll();
    }

    @Test
    void search_shouldReturnEmptyListItemDto() {
        Integer userIdRequesting = user1.getId();
        String text = "";

        List<ItemDto> itemsFound = itemService.search(userIdRequesting, text);

        assertTrue(itemsFound.isEmpty());
    }

    @Test
    void addComment_shouldReturnCommentOutputDto() {
        Integer itemId = item1.getId();
        Integer userId = user2.getId();

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(itemRepository.existsById(itemId))
                .thenReturn(true);

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item1));

        Mockito.lenient().when(bookingRepository.findByBookerIdAndEndIsBefore(anyInt(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(lastBooking)));

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user2));

        Mockito.when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentOutputDto commentSaved = itemService.addComment(itemId, userId, comment);

        assertAll(
                () -> assertNotNull(commentSaved),
                () -> assertEquals(comment.getId(), commentSaved.getId()),
                () -> assertEquals(comment.getText(), commentSaved.getText()),
                () -> assertEquals(comment.getAuthor().getName(), commentSaved.getAuthorName()),
                () -> assertNotNull(commentSaved.getCreated())
        );
    }

    @Test
    void addComment_shouldThrowUnavailableItemBookingException() {
        Integer itemId = item1.getId();
        Integer userId = user2.getId();

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(itemRepository.existsById(itemId))
                .thenReturn(true);

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item1));

        Mockito.when(bookingRepository.findByBookerIdAndEndIsBefore(anyInt(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        assertThrows(UnavailableItemBookingException.class, () -> itemService.addComment(itemId, userId, comment));
    }
}
