package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    @Test
    void testSerialize() throws Exception {
        ItemDto itemDto = new ItemDto(1, "Balalaika", "Brand new balalaika", true);

        JsonContent<ItemDto> itemDtoSaved = jacksonTester.write(itemDto);

        assertThat(itemDtoSaved).hasJsonPath("$.id");
        assertThat(itemDtoSaved).hasJsonPath("$.name");
        assertThat(itemDtoSaved).hasJsonPath("$.description");
        assertThat(itemDtoSaved).hasJsonPath("$.available");
        assertThat(itemDtoSaved).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId());
        assertThat(itemDtoSaved).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(itemDtoSaved).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(itemDtoSaved).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
    }

}
