package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithRequestId;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestDtoOutputTest {

    @Autowired
    private JacksonTester<RequestDtoOutput> jacksonTester;

    @Test
    void testSerialize() throws Exception {
        User user = new User(2, "Shaun", "shaun@ya.ru");
        Request request = new Request(1, "Looking for a balalaika", LocalDateTime.now(), user);

        ItemDto itemDto = new ItemDto(1, "Balalaika", "Brand new balalaika", true);
        ItemDtoWithRequestId item = new ItemDtoWithRequestId(itemDto, 1);
        List<ItemDtoWithRequestId> items = new ArrayList<>(List.of(item));

        RequestDtoOutput requestDtoInput = new RequestDtoOutput(request, items);

        JsonContent<RequestDtoOutput> requestDtoOutputSaved = jacksonTester.write(requestDtoInput);

        assertThat(requestDtoOutputSaved).hasJsonPath("$.id");
        assertThat(requestDtoOutputSaved).hasJsonPath("$.description");
        assertThat(requestDtoOutputSaved).hasJsonPath("$.created");
        assertThat(requestDtoOutputSaved).hasJsonPath("$.items");

        assertThat(requestDtoOutputSaved).hasJsonPathValue("$.created");

        assertThat(requestDtoOutputSaved).extractingJsonPathNumberValue("$.id").isEqualTo(request.getId());
        assertThat(requestDtoOutputSaved).extractingJsonPathStringValue("$.description").isEqualTo(request.getDescription());
        assertThat(requestDtoOutputSaved).extractingJsonPathArrayValue("$.items").hasSize(1);
    }
}
