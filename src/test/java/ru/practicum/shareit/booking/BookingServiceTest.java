package ru.practicum.shareit.booking;

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
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.IllegalItemBookingException;
import ru.practicum.shareit.exceptions.IllegalSearchModeException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.UnavailableItemBookingException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
public class BookingServiceTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Spy
    private BookingMapperImpl bookingMapper;

    private Item item;
    private User user;
    private User userBooker;
    private Booking booking;

    @BeforeEach
    void setup() {
        user = new User(1, "Jason", "jason@ya.ru");
        item = new Item(1, "Balalaika", "Brand new balalaika", true, user, null);
        userBooker = new User(2, "Shaun", "shaun@ya.ru");
        booking = new Booking(1, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2), item, userBooker, BookingStatus.CANCELED);
    }

    @Test
    void add_shouldReturnBookingDtoOutput() {
        Integer bookerId = userBooker.getId();
        BookingDtoInput bookingDtoInput = new BookingDtoInput(userBooker.getId(), item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));

        Mockito.when(userRepository.existsById(bookerId))
                .thenReturn(true);

        Mockito.when(itemRepository.existsById(bookingDtoInput.getItemId()))
                .thenReturn(true);

        Mockito.when(itemRepository.findById(bookingDtoInput.getItemId()))
                .thenReturn(Optional.ofNullable(item));

        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(userBooker));

        Mockito.lenient()
                .when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingDtoOutput bookingDtoOutputSaved = bookingService.add(bookerId, bookingDtoInput);

        assertNotNull(bookingDtoOutputSaved);
    }

    @Test
    void add_throwsUnavailableItemBookingException() {
        item = new Item(1, "Balalaika", "Brand new balalaika", false, user, null);

        Integer bookerId = userBooker.getId();
        BookingDtoInput bookingDtoInput = new BookingDtoInput(userBooker.getId(), item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));

        Mockito.when(userRepository.existsById(bookerId))
                .thenReturn(true);

        Mockito.when(itemRepository.existsById(bookingDtoInput.getItemId()))
                .thenReturn(true);

        Mockito.when(itemRepository.findById(bookingDtoInput.getItemId()))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(UnavailableItemBookingException.class, () -> bookingService.add(bookerId, bookingDtoInput));
    }

    @Test
    void add_throwsIllegalItemBookingException() {
        item = new Item(1, "Balalaika", "Brand new balalaika", true, userBooker, null);

        Integer bookerId = userBooker.getId();
        BookingDtoInput bookingDtoInput = new BookingDtoInput(userBooker.getId(), item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));

        Mockito.when(userRepository.existsById(bookerId))
                .thenReturn(true);

        Mockito.when(itemRepository.existsById(bookingDtoInput.getItemId()))
                .thenReturn(true);

        Mockito.when(itemRepository.findById(bookingDtoInput.getItemId()))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(IllegalItemBookingException.class, () -> bookingService.add(bookerId, bookingDtoInput));
    }

    @Test
    void add_throwsObjectNotFoundException() {
        Integer bookerId = userBooker.getId();
        BookingDtoInput bookingDtoInput = new BookingDtoInput(userBooker.getId(), item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));

        Mockito.when(userRepository.existsById(bookerId))
                .thenReturn(true);

        Mockito.when(itemRepository.existsById(bookingDtoInput.getItemId()))
                .thenReturn(true);

        Mockito.when(itemRepository.findById(bookingDtoInput.getItemId()))
                .thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.add(bookerId, bookingDtoInput));
    }

    @Test
    void setApprove_shouldReturnBookingDtoOutput() {
        booking = new Booking(1, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2), item, userBooker, BookingStatus.WAITING);

        Integer bookingId = booking.getId();
        Integer userId = user.getId();
        boolean isApproved = true;

        Mockito.when(bookingRepository.existsById(bookingId))
                .thenReturn(true);

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.ofNullable(booking));

        Mockito.when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingDtoOutput bookingDtoOutputSaved = bookingService.setApprove(bookingId, userId, isApproved);

        assertNotNull(bookingDtoOutputSaved);
    }

    @Test
    void setApprove_throwsIllegalItemBookingException() {
        item = new Item(1, "Balalaika", "Brand new balalaika", true, userBooker, null);
        booking = new Booking(1, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2), item, userBooker, BookingStatus.WAITING);

        Integer bookingId = booking.getId();
        Integer userId = user.getId();
        boolean isApproved = true;

        Mockito.when(bookingRepository.existsById(bookingId))
                .thenReturn(true);

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.ofNullable(booking));

        assertThrows(IllegalItemBookingException.class, () -> bookingService.setApprove(bookingId, userId, isApproved));
    }

    @Test
    void setApprove_throwsUnavailableItemBookingException() {
        Integer bookingId = booking.getId();
        Integer userId = user.getId();
        boolean isApproved = true;

        Mockito.when(bookingRepository.existsById(bookingId))
                .thenReturn(true);

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.ofNullable(booking));

        assertThrows(UnavailableItemBookingException.class, () -> bookingService.setApprove(bookingId, userId, isApproved));
    }

    @Test
    void setApprove_throwsObjectNotFoundException() {
        booking = new Booking(1, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2), item, userBooker, BookingStatus.WAITING);

        Integer bookingId = booking.getId();
        Integer userId = user.getId();
        boolean isApproved = true;

        Mockito.when(bookingRepository.existsById(bookingId))
                .thenReturn(true);

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.setApprove(bookingId, userId, isApproved));
    }

    @Test
    void get_shouldReturnBookingDtoOutput() {
        Integer bookingId = booking.getId();
        Integer userId = user.getId();

        Mockito.when(bookingRepository.existsById(bookingId))
                .thenReturn(true);

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.ofNullable(booking));

        BookingDtoOutput bookingDtoOutputSaved = bookingService.get(bookingId, userId);

        assertNotNull(bookingDtoOutputSaved);
    }

    @Test
    void get_throwsObjectNotFoundException() {
        Integer bookingId = booking.getId();
        Integer userId = user.getId();

        Mockito.when(bookingRepository.existsById(bookingId))
                .thenReturn(true);

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.get(bookingId, userId));
    }

    @Test
    void getAll_shouldReturnListOfBookings() {
        Integer userId = user.getId();
        Integer from = 1;
        Integer size = 1;

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        Mockito.when(bookingRepository.findByBookerId(any(), any()))
                .thenReturn((new PageImpl<>(List.of(booking))));
        List<BookingDtoOutput> bookingDtoListSaved = bookingService.getAll("ALL", userId, from, size);
        assertEquals(bookingDtoListSaved.size(), 1);

        Mockito.when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn((new PageImpl<>(List.of(booking))));
        bookingDtoListSaved = bookingService.getAll("CURRENT", userId, from, size);
        assertEquals(bookingDtoListSaved.size(), 1);

        Mockito.when(bookingRepository.findByBookerIdAndEndIsBefore(any(), any(), any(Pageable.class)))
                .thenReturn((new PageImpl<>(List.of(booking))));
        bookingDtoListSaved = bookingService.getAll("PAST", userId, from, size);
        assertEquals(bookingDtoListSaved.size(), 1);

        Mockito.when(bookingRepository.findByBookerIdAndStartIsAfter(any(), any(), any()))
                .thenReturn((new PageImpl<>(List.of(booking))));
        bookingDtoListSaved = bookingService.getAll("FUTURE", userId, from, size);
        assertEquals(bookingDtoListSaved.size(), 1);

        Mockito.when(bookingRepository.findByBookerIdAndStatus(any(), any(), any()))
                .thenReturn((new PageImpl<>(List.of(booking))));
        bookingDtoListSaved = bookingService.getAll("WAITING", userId, from, size);
        assertEquals(bookingDtoListSaved.size(), 1);

        Mockito.when(bookingRepository.findByBookerIdAndStatus(any(), any(), any()))
                .thenReturn((new PageImpl<>(List.of(booking))));
        bookingDtoListSaved = bookingService.getAll("REJECTED", userId, from, size);
        assertEquals(bookingDtoListSaved.size(), 1);
    }

    @Test
    void getAll_throwsIllegalSearchModeException() {
        Integer userId = user.getId();
        Integer from = 1;
        Integer size = 1;

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(IllegalSearchModeException.class, () -> bookingService.getAll("UNKNOWN", userId, from, size));
    }

    @Test
    void getAllByOwner_shouldReturnListOfBookings() {
        Integer userId = user.getId();
        Integer from = 1;
        Integer size = 1;

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        Mockito.when(bookingRepository.findByItemOwnerId(any(), any()))
                .thenReturn((new PageImpl<>(List.of(booking))));
        List<BookingDtoOutput> bookingDtoListSaved = bookingService.getAllByOwner("ALL", userId, from, size);
        assertEquals(bookingDtoListSaved.size(), 1);

        Mockito.when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn((new PageImpl<>(List.of(booking))));
        bookingDtoListSaved = bookingService.getAllByOwner("CURRENT", userId, from, size);
        assertEquals(bookingDtoListSaved.size(), 1);

        Mockito.when(bookingRepository.findByItemOwnerIdAndEndIsBefore(any(), any(), any(Pageable.class)))
                .thenReturn((new PageImpl<>(List.of(booking))));
        bookingDtoListSaved = bookingService.getAllByOwner("PAST", userId, from, size);
        assertEquals(bookingDtoListSaved.size(), 1);

        Mockito.when(bookingRepository.findByItemOwnerIdAndStartIsAfter(any(), any(), any()))
                .thenReturn((new PageImpl<>(List.of(booking))));
        bookingDtoListSaved = bookingService.getAllByOwner("FUTURE", userId, from, size);
        assertEquals(bookingDtoListSaved.size(), 1);

        Mockito.when(bookingRepository.findByItemOwnerIdAndStatus(any(), any(), any()))
                .thenReturn((new PageImpl<>(List.of(booking))));
        bookingDtoListSaved = bookingService.getAllByOwner("WAITING", userId, from, size);
        assertEquals(bookingDtoListSaved.size(), 1);

        Mockito.when(bookingRepository.findByItemOwnerIdAndStatus(any(), any(), any()))
                .thenReturn((new PageImpl<>(List.of(booking))));
        bookingDtoListSaved = bookingService.getAllByOwner("REJECTED", userId, from, size);
        assertEquals(bookingDtoListSaved.size(), 1);
    }

    @Test
    void getAllByOwner_throwsIllegalSearchModeException() {
        Integer userId = user.getId();
        Integer from = 1;
        Integer size = 1;

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(IllegalSearchModeException.class, () -> bookingService.getAllByOwner("UNKNOWN", userId, from, size));
    }
}
