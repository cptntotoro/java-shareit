package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestDtoInputTest {

    @Autowired
    private JacksonTester<RequestDtoInput> jacksonTester;

    @Test
    void testSerialize() throws Exception {
        RequestDtoInput requestDtoInput = new RequestDtoInput("Looking for a balalaika");

        JsonContent<RequestDtoInput> requestDtoInputSaved = jacksonTester.write(requestDtoInput);

        assertThat(requestDtoInputSaved).hasJsonPath("$.description");
        assertThat(requestDtoInputSaved).extractingJsonPathStringValue("$.description").isEqualTo(requestDtoInput.getDescription());
    }
}
