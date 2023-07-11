package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.ParameterNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User add(User user) {
        for (User user1 : userRepository.getAll()) {
            if (user1.getEmail().equals(user.getEmail())) {
                throw new DuplicateException("Почта уже существует");
            }
            log.info("Почта {} уже существует", user.getEmail());
        }
        log.info("Пользователь {} создан", user.getName());
        return userRepository.add(user);
    }

    @Override
    public User update(Long id, UserDto userDto) {
        User user = getById(id);
        updateName(user, userDto);
        updateEmail(user, userDto);
        log.info("Пользователь обновлен {}", user);
        return userRepository.update(user);
    }

    @Override
    public void delete(long id) {
        validationId(id);
        log.info("Пользователь {} удален", userRepository.getById(id));
        userRepository.delete(id);
    }

    @Override
    public User getById(long id) {
        validationId(id);
        log.info("Запрошен пользователь {} ", userRepository.getById(id));
        return userRepository.getById(id);
    }

    @Override
    public List<User> getAll() {
        log.info("Запрошен список пользователей, количество - {}", userRepository.getAll().size());
        return userRepository.getAll();
    }

    private void validationId(long id) {
        try {
            userRepository.getById(id).getId();
        } catch (NullPointerException e) {
            if (id <= 0) {
                throw new IncorrectParameterException("id меньше 0");
            } else {
                throw new ParameterNotFoundException("id не найден");
            }
        }
    }

    private void updateName(User user, UserDto userDto) {
        try {
            if (!userDto.getName().isBlank()) {
                user.setName(userDto.getName());
            }
        } catch (NullPointerException e) {
            log.error("Пустое поле");
            return;
        }
    }

    private void updateEmail(User user, UserDto userDto) {
        try {
            if (!userDto.getEmail().isBlank()) {
                for (User user1 : userRepository.getAll()) {
                    if (user1.getEmail().equals(userDto.getEmail()) && (!user.getId().equals(user1.getId()))) {
                        throw new DuplicateException("Эта почта уже используется, введите другую.");
                    }
                    log.info("{} - Эта почта уже используется, введите другую.", user.getEmail());
                }
                user.setEmail(userDto.getEmail());
            }
        } catch (NullPointerException e) {
            log.error("Пустое поле");
            return;
        }
    }
}
