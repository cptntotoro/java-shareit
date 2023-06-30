package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestDtoShortTest {

    @Autowired
    private JacksonTester<RequestDtoShortOutput> jacksonTester;

    @Test
    void testSerialize() throws Exception {
        RequestDtoShortOutput requestDtoInput = new RequestDtoShortOutput(1, "Looking for a balalaika", LocalDateTime.now());

        JsonContent<RequestDtoShortOutput> requestDtoSaved = jacksonTester.write(requestDtoInput);

        assertThat(requestDtoSaved).hasJsonPath("$.id");
        assertThat(requestDtoSaved).hasJsonPath("$.description");
        assertThat(requestDtoSaved).hasJsonPath("$.created");

        assertThat(requestDtoSaved).hasJsonPathValue("$.created");

        assertThat(requestDtoSaved).extractingJsonPathNumberValue("$.id").isEqualTo(requestDtoInput.getId());
        assertThat(requestDtoSaved).extractingJsonPathStringValue("$.description").isEqualTo(requestDtoInput.getDescription());
    }
}
