package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto add(UserDto userDto) {
        if (userRepository.checkEmail(userDto)) {
            throw new DuplicateException("Почта существует");
        }
        User user = userRepository.add(userMapper.toUser(userDto));
        log.info("Создан пользователь {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.getById(id);
        updateName(user, userDto);
        updateEmail(user, userDto);
        log.info("Пользователь обновлен {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    public void delete(long id) {
        userRepository.delete(id);
        log.info("Пользователь {} удален", userRepository.getById(id));
    }

    @Override
    public User getById(long id) {
        User user = userRepository.getById(id);
        log.info("Запрошен пользователь {} ", userRepository.getById(id));
        return user;
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> userDto = new ArrayList<>();
        for (User users : userRepository.getAll()) {
            UserDto userDtoNew = userMapper.toUserDto(users);
            userDto.add(userDtoNew);
        }
        log.info("Запрошен список пользователей, количество - {}", userRepository.getAll().size());
        return userDto;
    }

    private void updateName(User user, UserDto userDto) {
        if (userDto.getName() == null) {
            return;
        } else {
            user.setName(userDto.getName());
        }
    }

    private void updateEmail(User user, UserDto userDto) {
        if (userDto.getEmail() == null) {
            return;
        } else {
            for (User user1 : userRepository.getAll()) {
                if (user1.getEmail().equals(userDto.getEmail()) && (!user.getId().equals(user1.getId()))) {
                    throw new DuplicateException("Эта почта уже используется, введите другую.");
                }
            }
            user.setEmail(userDto.getEmail());
        }
    }
}
