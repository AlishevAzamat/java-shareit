package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class UserIntegrationTest {
    @Autowired
    private UserController userController;
    private final UserDto userDtoTestOne = UserDto.builder()
            .id(0L)
            .name("TestName1")
            .email("test1@test.test")
            .build();
    private final UserDto userDtoTestTwo = UserDto.builder()
            .id(0L)
            .name("TestName2")
            .email("test2@test.test")
            .build();

    @Test
    @DisplayName("Проверяем метод GET(все id) контроллера user.")
    void userGet_compareResult_whenObjectCorrect() {
        UserDto userDtoOne = userController.add(userDtoTestOne);
        UserDto userDtoTwo = userController.add(userDtoTestTwo);

        List<UserDto> userDtos = userController.getAll();
        assertEquals(2, userDtos.size(), "Размер списка должен быть равен 2.");
        assertEquals(userDtoOne, userDtos.get(0), "Пользователи должны совпадать.");
        assertEquals(userDtoTwo, userDtos.get(1), "Пользователи должны совпадать.");
    }

    @Test
    @DisplayName("Проверяем метод GET(id) контроллера user.")
    void userGetById_compareResult_whenObjectCorrect() {
        UserDto userDtoOne = userController.add(userDtoTestOne);
        UserDto userDtoTwo = userController.add(userDtoTestTwo);

        assertNotEquals(userDtoTwo, userController.getById(1), "Пользователи не должны совпадать");
        assertNotEquals(userDtoOne, userController.getById(2), "Пользователи не должны совпадать.");
    }

    @Test
    @DisplayName("Проверяем метод POST контроллера user.")
    void userAdd_compareResult_whenObjectCorrect() {
        UserDto userDtoOne = userController.add(userDtoTestOne);
        UserDto userDtoTwo = userController.add(userDtoTestTwo);

        assertEquals(1, userDtoOne.getId(), "Id должен быть равен 1.");
        assertEquals("TestName1", userDtoOne.getName(), "Имя должно совпадать.");
        assertEquals("test1@test.test", userDtoOne.getEmail(), "Почта должна совпадать.");
        assertEquals("TestName2", userDtoTwo.getName(), "Имя должно совпадать.");
        assertEquals("test2@test.test", userDtoTwo.getEmail(), "Почта должна совпадать.");
    }

    @Test
    @DisplayName("Проверяем метод PATCH контроллера user.")
    void userUpdate_compareResult_whenObjectCorrect() {
        UserDto userDto = userController.add(userDtoTestOne);
        UserDto updateName = UserDto.builder().name("update").build();
        UserDto updateEmail = UserDto.builder().email("update@update.com").build();

        userController.update(1, updateName);

        User updateNameTest = userController.getById(1);
        assertNotEquals(userDto, updateNameTest, "Пользователи не должны совпадать.");
        assertEquals(1, updateNameTest.getId(), "Id должен быть равен 1.");
        assertEquals("update", updateNameTest.getName(), "Имя должно совпадать.");

        userController.update(1, updateEmail);

        User updateEmailTest = userController.getById(1);
        assertNotEquals(userDto, updateNameTest, "Пользователи не должны совпадать.");
        assertEquals(1, updateNameTest.getId(), "Id должен быть равен 1.");
        assertEquals("update@update.com", updateEmailTest.getEmail(), "Email должен совпадать.");
    }

    @Test
    @DisplayName("Проверяем метод DELETE контроллера user.")
    void userDelete_compareResult_whenObjectCorrect() {
        userController.add(userDtoTestOne);
        UserDto userDto = userController.add(userDtoTestTwo);
        userController.deleteUser(1);

        List<UserDto> userDtosOne = userController.getAll();

        assertEquals(1, userDtosOne.size(), "Размер списка должен быть равен 1.");
        assertEquals(userDto, userDtosOne.get(0), "Пользователи должны совпадать.");

        userController.deleteUser(2);
        List<UserDto> userDtosThree = userController.getAll();

        assertEquals(0, userDtosThree.size(), "Размер списка должен быть равен 0.");
    }
}
