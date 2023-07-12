package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        return userMapper.toUserDto(userService.add(userDto));
    }

    @PatchMapping(value = "/{userId}")
    public UserDto update(@PathVariable("userId") long id, @RequestBody UserDto userDto) {
        return userMapper.toUserDto(userService.update(id, userDto));
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") long id) {
        return userMapper.toUserDto(userService.getById(id));
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(toList());
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long id) {
        userService.delete(id);
    }
}
