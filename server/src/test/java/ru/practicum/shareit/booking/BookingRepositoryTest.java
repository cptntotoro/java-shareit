package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private User user;

    @BeforeEach
    void setup() {
        this.item = addItem("Balalaika", "Brand new balalaika", true, user);
        this.user = addUser("Jason", "jason@ya.ru");
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findByBooker_Id() {
        Booking booking = addBooking(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), user, item, BookingStatus.CANCELED);
        Page<Booking> bookingPageSaved = bookingRepository.findByBookerId(user.getId(), PageRequest.of(0, 1, Sort.unsorted()));

        assertNotNull(bookingPageSaved);
        assertEquals(1, bookingPageSaved.getSize());

        Booking bookingSaved = bookingPageSaved.getContent().get(0);

        assertNotNull(bookingSaved);
        assertEquals(booking.getBooker().getId(), bookingSaved.getBooker().getId());
    }


    @Test
    void findByBooker_IdAndStartIsAfter() {
        addBooking(LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(6), user, item, BookingStatus.CANCELED);
        Page<Booking> bookingPageSaved = bookingRepository.findByBookerIdAndStartIsAfter(user.getId(), LocalDateTime.now(), PageRequest.of(0, 1, Sort.unsorted()));

        assertNotNull(bookingPageSaved);
        assertEquals(1, bookingPageSaved.getSize());

        Booking bookingSaved = bookingPageSaved.getContent().get(0);

        assertNotNull(bookingSaved);
        assertTrue(bookingSaved.getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    void findByBooker_IdAndEndIsBefore() {
        addBooking(LocalDateTime.now().minusDays(6), LocalDateTime.now().minusDays(4), user, item, BookingStatus.CANCELED);
        Page<Booking> bookingPageSaved = bookingRepository.findByBookerIdAndEndIsBefore(user.getId(), LocalDateTime.now(), PageRequest.of(0, 1, Sort.unsorted()));

        assertNotNull(bookingPageSaved);
        assertEquals(1, bookingPageSaved.getSize());

        Booking bookingSaved = bookingPageSaved.getContent().get(0);

        assertNotNull(bookingSaved);
        assertTrue(bookingSaved.getStart().isBefore(LocalDateTime.now()));
    }

    @Test
    void findByBooker_IdAndStatus() {
        Booking booking = addBooking(LocalDateTime.now().minusDays(6), LocalDateTime.now().minusDays(4), user, item, BookingStatus.WAITING);
        Page<Booking> bookingPageSaved = bookingRepository.findByBookerIdAndStatus(user.getId(), BookingStatus.WAITING, PageRequest.of(0, 1, Sort.unsorted()));

        assertNotNull(bookingPageSaved);
        assertEquals(1, bookingPageSaved.getSize());

        Booking bookingSaved = bookingPageSaved.getContent().get(0);

        assertNotNull(bookingSaved);
        assertEquals(booking.getBooker().getId(), bookingSaved.getBooker().getId());
        assertEquals(BookingStatus.WAITING, bookingSaved.getStatus());
    }

    @Test
    void findByBooker_IdAndStartIsBeforeAndEndIsAfter() {
        Booking booking = addBooking(LocalDateTime.now().minusDays(6), LocalDateTime.now().plusDays(4), user, item, BookingStatus.WAITING);
        Page<Booking> bookingPageSaved = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(user.getId(), LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 1, Sort.unsorted()));

        assertNotNull(bookingPageSaved);
        assertEquals(1, bookingPageSaved.getSize());

        Booking bookingSaved = bookingPageSaved.getContent().get(0);

        assertNotNull(bookingSaved);
        assertEquals(booking.getBooker().getId(), bookingSaved.getBooker().getId());
        assertTrue(bookingSaved.getStart().isBefore(LocalDateTime.now()));
        assertTrue(bookingSaved.getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    void findByItemOwnerId() {
        item.setOwner(userRepository.findById(user.getId()).get());

        addBooking(LocalDateTime.now().minusDays(6), LocalDateTime.now().plusDays(4), user, item, BookingStatus.WAITING);
        Page<Booking> bookingPageSaved = bookingRepository.findByItemOwnerId(item.getOwner().getId(), PageRequest.of(0, 1, Sort.unsorted()));

        assertNotNull(bookingPageSaved);
        assertEquals(1, bookingPageSaved.getSize());

        Booking bookingSaved = bookingPageSaved.getContent().get(0);

        assertNotNull(bookingSaved);

        bookingSaved.getItem().setOwner(userRepository.findById(user.getId()).get());

        assertEquals(item.getOwner().getId(), bookingSaved.getItem().getOwner().getId());
    }

    @Test
    void findByItemOwnerIdAndStartIsAfter() {
        item.setOwner(userRepository.findById(user.getId()).get());

        addBooking(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4), user, item, BookingStatus.WAITING);
        Page<Booking> bookingPageSaved = bookingRepository.findByItemOwnerIdAndStartIsAfter(item.getOwner().getId(), LocalDateTime.now(), PageRequest.of(0, 1, Sort.unsorted()));

        assertNotNull(bookingPageSaved);
        assertEquals(1, bookingPageSaved.getSize());

        Booking bookingSaved = bookingPageSaved.getContent().get(0);

        assertNotNull(bookingSaved);

        bookingSaved.getItem().setOwner(userRepository.findById(user.getId()).get());

        assertEquals(item.getOwner().getId(), bookingSaved.getItem().getOwner().getId());
        assertTrue(bookingSaved.getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    void findByItemOwnerIdAndEndIsBefore() {
        item.setOwner(userRepository.findById(user.getId()).get());

        addBooking(LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(2), user, item, BookingStatus.WAITING);
        Page<Booking> bookingPageSaved = bookingRepository.findByItemOwnerIdAndEndIsBefore(item.getOwner().getId(), LocalDateTime.now(), PageRequest.of(0, 1, Sort.unsorted()));

        assertNotNull(bookingPageSaved);
        assertEquals(1, bookingPageSaved.getSize());

        Booking bookingSaved = bookingPageSaved.getContent().get(0);

        assertNotNull(bookingSaved);

        bookingSaved.getItem().setOwner(userRepository.findById(user.getId()).get());

        assertEquals(item.getOwner().getId(), bookingSaved.getItem().getOwner().getId());
        assertTrue(bookingSaved.getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    void findByItemOwnerIdAndStatus() {
        item.setOwner(userRepository.findById(user.getId()).get());

        addBooking(LocalDateTime.now().minusDays(6), LocalDateTime.now().plusDays(4), user, item, BookingStatus.WAITING);
        Page<Booking> bookingPageSaved = bookingRepository.findByItemOwnerIdAndStatus(item.getOwner().getId(), BookingStatus.WAITING, PageRequest.of(0, 1, Sort.unsorted()));

        assertNotNull(bookingPageSaved);
        assertEquals(1, bookingPageSaved.getSize());

        Booking bookingSaved = bookingPageSaved.getContent().get(0);

        assertNotNull(bookingSaved);

        bookingSaved.getItem().setOwner(userRepository.findById(user.getId()).get());

        assertEquals(item.getOwner().getId(), bookingSaved.getItem().getOwner().getId());
        assertEquals(BookingStatus.WAITING, bookingSaved.getStatus());
    }

    @Test
    void findByItemOwnerIdAndStartIsBeforeAndEndIsAfter() {
        item.setOwner(userRepository.findById(user.getId()).get());

        addBooking(LocalDateTime.now().minusDays(4), LocalDateTime.now().plusDays(2), user, item, BookingStatus.WAITING);
        Page<Booking> bookingPageSaved = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(item.getOwner().getId(), LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 1, Sort.unsorted()));

        assertNotNull(bookingPageSaved);
        assertEquals(1, bookingPageSaved.getSize());

        Booking bookingSaved = bookingPageSaved.getContent().get(0);

        assertNotNull(bookingSaved);

        bookingSaved.getItem().setOwner(userRepository.findById(user.getId()).get());

        assertEquals(item.getOwner().getId(), bookingSaved.getItem().getOwner().getId());
        assertTrue(bookingSaved.getStart().isBefore(LocalDateTime.now()));
        assertTrue(bookingSaved.getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    void findByItemIdAndStartIsAfterAndStatusNot() {
        addBooking(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4), user, item, BookingStatus.WAITING);
        List<Booking> bookingPageSaved = bookingRepository.findByItemIdAndStartIsAfterAndStatusNot(item.getId(), LocalDateTime.now(), BookingStatus.REJECTED, Sort.unsorted());

        assertNotNull(bookingPageSaved);
        assertEquals(1, bookingPageSaved.size());

        Booking bookingSaved = bookingPageSaved.get(0);

        assertNotNull(bookingSaved);
        assertEquals(item.getId(), bookingSaved.getItem().getId());
        assertTrue(bookingSaved.getStart().isAfter(LocalDateTime.now()));
        assertNotEquals(bookingSaved.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void findByItemIdAndStartIsBeforeAndStatusNot() {
        addBooking(LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(4), user, item, BookingStatus.WAITING);
        List<Booking> bookingPageSaved = bookingRepository.findByItemIdAndStartIsBeforeAndStatusNot(item.getId(), LocalDateTime.now(), BookingStatus.REJECTED, Sort.unsorted());

        assertNotNull(bookingPageSaved);
        assertEquals(1, bookingPageSaved.size());

        Booking bookingSaved = bookingPageSaved.get(0);

        assertNotNull(bookingSaved);
        assertEquals(item.getId(), bookingSaved.getItem().getId());
        assertTrue(bookingSaved.getStart().isBefore(LocalDateTime.now()));
        assertNotEquals(bookingSaved.getStatus(), BookingStatus.REJECTED);
    }

    private Item addItem(String name, String description, boolean available, User owner) {
        Item itemToSave = new Item();
        itemToSave.setName(name);
        itemToSave.setDescription(description);
        itemToSave.setAvailable(available);
        itemToSave.setOwner(owner);
        return itemRepository.save(itemToSave);
    }

    private User addUser(String name, String email) {
        User userToSave = new User();
        userToSave.setName(name);
        userToSave.setEmail(email);
        return userRepository.save(userToSave);
    }

    private Booking addBooking(LocalDateTime start, LocalDateTime end, User user, Item item, BookingStatus bookingStatus) {
        Booking bookingToSave = new Booking();
        bookingToSave.setStart(start);
        bookingToSave.setEnd(end);
        bookingToSave.setBooker(user);
        bookingToSave.setItem(item);
        bookingToSave.setStatus(bookingStatus);
        return bookingRepository.save(bookingToSave);
    }
}