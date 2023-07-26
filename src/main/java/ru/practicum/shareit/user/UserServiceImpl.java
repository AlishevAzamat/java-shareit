package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ParameterNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto add(UserDto userDto) {
        User user = userRepository.save(userMapper.toUser(userDto));
        log.info("Создан пользователь {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = getById(id);
        updateName(user, userDto);
        updateEmail(user, userDto);
        userRepository.save(user);
        log.info("Пользователь обновлен {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    public void delete(long id) {
        getById(id);
        userRepository.deleteById(id);
        log.info("Пользователь под ID - {} удален", id);
    }

    @Override
    public User getById(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ParameterNotFoundException("Пользователь не найден");
        } else {
            log.info("Запрошен пользователь {} ", user);
            return user.get();
        }
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> userDto = new ArrayList<>();
        for (User users : userRepository.findAll()) {
            UserDto userDtoNew = userMapper.toUserDto(users);
            userDto.add(userDtoNew);
        }
        log.info("Запрошен список пользователей, количество - {}", userRepository.findAll().size());
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
            user.setEmail(userDto.getEmail());
        }
    }
}
