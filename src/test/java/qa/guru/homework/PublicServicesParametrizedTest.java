package qa.guru.homework;

import java.util.List;
import java.util.stream.Stream;
import com.codeborne.selenide.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;


@DisplayName("Тестирование сайта госуслуг с параметризацией")
public class PublicServicesParametrizedTest {

    @BeforeEach
    void init() {
        Selenide.open("https://www.gosuslugi.ru");
    }

    @AfterEach
    void tearDown() {
        Selenide.sleep(3000);
    }
    //----------------------------------------------------------------------------

    @ValueSource(strings = {"Санкт-Петербург", "Новосибирск"})
    @Tags({
            @Tag("SMOKE")
    })
    @ParameterizedTest(name = "На сайте госуслуг успешно удалось выбрать/изменить город {0}")
    void successfulChoiceOfCityTest(String city) {
        SelenideElement itemCity = $$("div.header a[role='button']").last();
        itemCity.click();

        SelenideElement popupElement = $("div.popup").shouldBe(visible);
        popupElement.$("#app-radio-1").click();
        popupElement.$("input.search-input").setValue(city);
        popupElement.$("div.item-container").click();
        popupElement.$("button").click();

        itemCity.shouldHave(text(city));
    }
    //----------------------------------------------------------------------------

    @ParameterizedTest(name = "Проверка нахождения элемента-футера {0}")
    @EnumSource(Footer.class)
    void testFooterElements(Footer footer) {

        SelenideElement footerElement = $("#footer-wrapper").shouldBe(visible);
        footerElement.$(withTagAndText("a", footer.getStringValue()));
    }
    //----------------------------------------------------------------------------

    @CsvFileSource(resources = "/testdata/successfulSearchWithOldWayTest.csv")
    @ParameterizedTest(name  = "На сайте госуслуг старый поиск работает корректно")
    void successfulSearchWithOldWayTest(String question, String resultOne, String resultTwo) {
        // Открываем поиск с ассистентом
        $("ul.links a[title^='Поиск']").shouldBe(visible).click();

        // Открываем старый поиск
        $("div.header-container")
                .$("button.old-search-button")
                .shouldBe(visible)
                .click();

        // Вводим в старый поиск
        switchTo().window(1);
        $("input.search-input").shouldBe(visible).click();
        $("input.search-input").setValue(question).pressEnter();

        // Проверяем результат
        SelenideElement pageSearch = $("portal-page-search").shouldBe(visible);
        pageSearch.$(withTagAndText("a", resultOne)).should(exist);
        pageSearch.$(withTagAndText("a", resultTwo)).should(exist);

        // Закрываем вкладку и переключаемя на начальную
        closeWindow();
        switchTo().window(0);
    }

    //----------------------------------------------------------------------------

    static Stream<Arguments> dataSuccessfulSearchWithBotTest() {
        return Stream.of(
                Arguments.of("паспорт", // вопрос
                     // Карта для ведения диалога с ботом
                     List.of(
                        List.of("Что вас интересует?", "Паспорт РФ"),
                        List.of("Вот что я могу предложить по паспорту РФ", "Оформить паспорт РФ"),
                        List.of("По какой причине нужно поменять паспорт?", "Показать ещё")
                    ),
                    // Список того, что предлагает бот
                    List.of(
                        "Достижение 20 или 45 лет", "Изменение фамилии, имени или отчества",
                        "Изменение даты и места рождения", "Хищение или утрата паспорта",
                         "Замена паспорта гражданина СССР"
                    )
                )
        );
    }

    @MethodSource("dataSuccessfulSearchWithBotTest")
    @Tags({
            @Tag("NEW_FUNCTIONAL")
    })
    @ParameterizedTest(name = "На сайте госуслуг бот-помощник работает корректно")
    void successfulSearchWithBotTest(String question,
                                     List<List<String>> questionAndAnswerFromRobot,
                                     List<String> variants) {

        // Открываем умного ассистента.
        $("ul.links a[title^='Поиск']").shouldBe(visible).click();

        // Вводим первоначальный вопрос.
        SelenideElement chatContainer = $("div.chat-container").shouldBe(visible);
        SelenideElement inputContainer = chatContainer.$(".input-container textarea");
        inputContainer.click();
        inputContainer.setValue(question).pressEnter();

        // Ведем небольшой диалог и выбираем по ходу варианты, что предлагает ассистент.
        SelenideElement messageButtons = chatContainer.$("div.message-buttons");
        for(var list: questionAndAnswerFromRobot) {
            messageButtons.shouldBe(visible);
            chatContainer.$$("div.robot").last().shouldBe(text(list.get(0)));

            messageButtons.$(withText(list.get(1))).shouldBe(visible).click();
        }

        // Проверяем последний список того, что предлагает ассистент.
        messageButtons.shouldBe(visible);
        for(var variant: variants) {
            messageButtons.$(withText(variant)).shouldBe(visible);
        }
    }
}
