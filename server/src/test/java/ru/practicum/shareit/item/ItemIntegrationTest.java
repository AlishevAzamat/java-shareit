package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class ItemIntegrationTest {
    private static final int USER_ID_ONE = 1;
    private static final Long USER_ID_TWO = 2L;
    private static final int FROM = 0;
    private static final int SIZE = 10;
    @Autowired
    private ItemController itemController;
    @Autowired
    private UserController userController;
    @Autowired
    private BookingController bookingController;

    User user = User.builder().id(1L).name("name").email("user@mail.ru").build();

    private final ItemDto itemDtoTestOne = ItemDto.builder()
            .id(0L)
            .name("TestName1")
            .description("testDescription1")
            .owner(user)
            .available(true)
            .build();

    private final ItemDto itemDtoTestTwo = ItemDto.builder()
            .id(0L)
            .name("TestName2")
            .description("testDescription2")
            .owner(user)
            .available(true)
            .build();

    private UserDto userDtoTestOne = UserDto.builder()
            .id(0L)
            .name("TestName")
            .email("test@test.test")
            .build();

    private UserDto userDtoTestTwo = UserDto.builder()
            .id(0L)
            .name("TestName2")
            .email("test@test.test2")
            .build();

    @BeforeEach
    void setUp() {
        userDtoTestOne = userController.add(userDtoTestOne);
        userDtoTestTwo = userController.add(userDtoTestTwo);
    }

    @Test
    @DisplayName("Проверяем метод GET(все id) контроллера item.")
    void getItems_compareResult_whenCorrect() {
        ItemDto itemDtoOne = itemController.add(USER_ID_ONE, itemDtoTestOne);
        ItemDto itemDtoTwo = itemController.add(USER_ID_ONE, itemDtoTestTwo);

        List<ItemDto> itemDtos = itemController.getItems(USER_ID_ONE, FROM, SIZE);
        assertEquals(2, itemDtos.size(), "Размер списка должен быть равен 2.");
        assertEquals(itemDtoOne, itemDtos.get(0), "Вещи должны совпадать.");
        assertEquals(itemDtoTwo, itemDtos.get(1), "Вещи должны совпадать.");
    }

    @Test
    @DisplayName("Проверяем метод GET(id) контроллера item.")
    void getItem_compareResult_whenCorrect() {
        ItemDto itemDtoOne = itemController.add(USER_ID_ONE, itemDtoTestOne);
        ItemDto itemDtoTwo = itemController.add(USER_ID_ONE, itemDtoTestTwo);
        assertEquals(itemDtoOne, itemController.getItem(USER_ID_ONE, 1), "Вещи должны совпадать.");
        assertNotEquals(itemDtoTwo, itemController.getItem(USER_ID_ONE, 1), "Вещи не должны совпадать");
        assertEquals(itemDtoTwo, itemController.getItem(USER_ID_ONE, 2), "Вещи должны совпадать.");
        assertNotEquals(itemDtoOne, itemController.getItem(USER_ID_ONE, 2), "Вещи не должны совпадать.");
    }

    @Test
    @DisplayName("Проверяем метод POST контроллера item.")
    void addItems_compareResult_whenCorrect() {
        ItemDto itemDtoOne = itemController.add(USER_ID_ONE, itemDtoTestOne);
        ItemDto itemDtoTwo = itemController.add(USER_ID_ONE, itemDtoTestTwo);

        assertEquals(itemDtoOne, itemController.getItem(1, USER_ID_ONE), "Вещи должны совпадать.");
        assertEquals(1, itemDtoOne.getId(), "Id должен быть равен 1.");
        assertEquals("TestName1", itemDtoOne.getName(), "Имя должно совпадать.");
        assertEquals("testDescription1", itemDtoOne.getDescription(), "Описание должно совпадать.");
        assertEquals(user.getId(), itemDtoOne.getOwner().getId(), "Id пользователя должно совпадать.");
        assertTrue(itemDtoOne.getAvailable(), "Доступность должна совпадать.");

        assertEquals(2, itemDtoTwo.getId(), "Id должен быть равен 1.");
        assertEquals("TestName2", itemDtoTwo.getName(), "Имя должно совпадать.");
        assertEquals("testDescription2", itemDtoTwo.getDescription(), "Описание должно совпадать.");
        assertEquals(userDtoTestOne.getId(), itemDtoTwo.getOwner().getId(), "Id пользователя должно совпадать.");
        assertTrue(itemDtoTwo.getAvailable(), "Доступность должна совпадать.");
    }

    @Test
    @DisplayName("Проверяем метод PATCH контроллера item.")
    void updateItems_compareResult_whenCorrect() {
        ItemDto itemDto = itemController.add(USER_ID_ONE, itemDtoTestOne);
        ItemDto updateName = ItemDto.builder().name("update").build();
        ItemDto updateDescription = ItemDto.builder().description("update").build();
        ItemDto updateAvailable = ItemDto.builder().available(false).build();

        itemController.update(USER_ID_ONE, 1L, updateName);

        ItemDto updateNameTest = itemController.getItem(1, USER_ID_ONE);
        assertNotEquals(itemDto, updateNameTest, "Пользователи не должны совпадать.");
        assertEquals(1, updateNameTest.getId(), "Id должен быть равен 1.");
        assertEquals("update", updateNameTest.getName(), "Имя должно совпадать.");

        itemController.update(1, USER_ID_ONE, updateDescription);

        ItemDto updateEmailTest = itemController.getItem(1, USER_ID_ONE);
        assertNotEquals(itemDto, updateNameTest, "Пользователи не должны совпадать.");
        assertEquals(1, updateNameTest.getId(), "Id должен быть равен 1.");
        assertEquals("update", updateEmailTest.getDescription(), "Описание должно совпадать.");

        itemController.update(1, USER_ID_ONE, updateAvailable);

        ItemDto updateAvailableTest = itemController.getItem(1, USER_ID_ONE);
        assertNotEquals(itemDto, updateNameTest, "Пользователи не должны совпадать.");
        assertEquals(1, updateNameTest.getId(), "Id должен быть равен 1.");
        assertFalse(updateAvailableTest.getAvailable(), "Доступность должна быть false.");
    }

    @Test
    @DisplayName("Проверяем метод GET(search) контроллера item.")
    void searchItems_compareResult_whenCorrect() {
        ItemDto itemDtoOne = itemController.add(USER_ID_ONE, itemDtoTestOne);
        List<ItemDto> dtosOne = itemController.searchItems(USER_ID_ONE, "TeS", FROM, SIZE);

        assertEquals(1, dtosOne.size(), "Размер списка должен равняться 1.");
        assertEquals(itemDtoOne, dtosOne.get(0), "Размер списка должен равняться 1.");

        ItemDto itemDtoTwo = itemController.add(USER_ID_ONE, itemDtoTestTwo);
        List<ItemDto> dtosTwo = itemController.searchItems(USER_ID_ONE, "TeS", FROM, SIZE);

        assertEquals(2, dtosTwo.size(), "Размер списка должен равняться 2.");
        assertEquals(itemDtoOne, dtosOne.get(0), "Размер списка должен равняться 1.");
        assertEquals(itemDtoTwo, dtosTwo.get(1), "Размер списка должен равняться 1.");
    }
}
