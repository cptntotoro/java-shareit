package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentOutputDtoTest {

    @Autowired
    private JacksonTester<CommentOutputDto> jacksonTester;

    @Test
    void testSerialize() throws Exception {
        CommentOutputDto commentDto = new CommentOutputDto(1, "Great balalaika! Thanks!", "Jason", LocalDateTime.now());

        JsonContent<CommentOutputDto> commentDtoSaved = jacksonTester.write(commentDto);

        assertThat(commentDtoSaved).hasJsonPath("$.id");
        assertThat(commentDtoSaved).hasJsonPath("$.text");
        assertThat(commentDtoSaved).hasJsonPath("$.authorName");
        assertThat(commentDtoSaved).hasJsonPath("$.created");

        assertThat(commentDtoSaved).extractingJsonPathNumberValue("$.id").isEqualTo(commentDto.getId());
        assertThat(commentDtoSaved).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
        assertThat(commentDtoSaved).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDto.getAuthorName());

        assertThat(commentDtoSaved).hasJsonPathValue("$.created");
    }

}