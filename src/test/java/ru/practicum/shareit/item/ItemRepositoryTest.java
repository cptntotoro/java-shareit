package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    private User itemOwner;
    private Request request;

    @BeforeEach
    void setup() {
        this.itemOwner = addUser("Jason", "jason@ya.ru");
        User userRequesting = addUser("Shaun", "shaun@ya.ru");
        this.request = addRequest("Looking for a balalaika.", LocalDateTime.now().minusDays(6), userRequesting);
        addItem("Balalaika", "Brand new balalaika", true, itemOwner, request);
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        requestRepository.deleteAll();
    }

    @Test
    void findByOwnerId() {
        List<Item> itemsSaved = itemRepository.findByOwnerId(itemOwner.getId());

        assertNotNull(itemsSaved);
        assertEquals(1, itemsSaved.size());

        Item itemSaved = itemsSaved.get(0);

        assertNotNull(itemSaved);
        assertEquals(itemOwner.getId(), itemSaved.getOwner().getId());
    }

    @Test
    void findByRequestId() {
        List<Item> itemsSaved = itemRepository.findByRequestId(request.getId());

        assertNotNull(itemsSaved);
        assertEquals(1, itemsSaved.size());

        Item itemSaved = itemsSaved.get(0);

        assertNotNull(itemSaved);
        assertEquals(request.getId(), itemSaved.getRequest().getId());
    }

    private Item addItem(String name, String description, boolean available, User owner, Request request) {
        Item itemToSave = new Item();
        itemToSave.setName(name);
        itemToSave.setDescription(description);
        itemToSave.setAvailable(available);
        itemToSave.setOwner(owner);
        itemToSave.setRequest(request);
        return itemRepository.save(itemToSave);
    }

    private User addUser(String name, String email) {
        User userToSave = new User();
        userToSave.setName(name);
        userToSave.setEmail(email);
        return userRepository.save(userToSave);
    }

    private Request addRequest(String description, LocalDateTime created, User userRequesting) {
        Request requestToSave = new Request();
        requestToSave.setDescription(description);
        requestToSave.setCreated(created);
        requestToSave.setRequestingUser(userRequesting);
        return requestRepository.save(requestToSave);
    }
}
