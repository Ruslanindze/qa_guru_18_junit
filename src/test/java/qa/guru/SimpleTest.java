package qa.guru;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@Disabled("JRASERVER-41589")
@DisplayName("Простые демонстрационные тесты")
public class SimpleTest {

    @Disabled("JRASERVER-41589")
    @Test
    @DisplayName("Проверка корректности работы метода methodEx()")
    void simpleTest() {
        String actual = methodEx();
        Assertions.assertEquals("1", actual);
    }

    String methodEx() {
        return "1";
    }
}
