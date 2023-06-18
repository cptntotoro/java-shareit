import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.ShareItApp;

public class ShareItAppTest {

    @Test
    void testMain() {
        Assertions.assertDoesNotThrow(ShareItApp::new);
        Assertions.assertDoesNotThrow(() -> ShareItApp.main(new String[]{}));
    }
}
