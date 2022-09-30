package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        if(Objects.isNull(userDto.getId())) {
            log.error("[{}]: User id is null for updating {} ", UserServiceImpl.class, userDto);
            throw new BadRequestException("User id is null for updating");
        }
        log.info("[{}]: Request to update user - {}", UserServiceImpl.class, userDto);

        Person user = userMapper.userDtoToPerson(userDto);

        log.info("[{}]: Mapped user: {}", UserServiceImpl.class, user);

        Person updatedUser = userRepository.save(user);

        log.info("[{}]: Updated user: {}", UserServiceImpl.class, updatedUser);

        return userMapper.personToUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        if(Objects.isNull(id)) {
            log.error("[{}]: User id is null for getting", UserServiceImpl.class);
            throw new BadRequestException("User id is null for getting");
        }

        log.info("[{}]: Request to get user with id - {}", UserServiceImpl.class, id);

        Person user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id - %d was not found", id)));

        log.info("[{}]: Got user: {}", UserServiceImpl.class, user);

        return userMapper.personToUserDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        if(Objects.isNull(id)) {
            log.error("[{}]: User id is null for deleting", UserServiceImpl.class);
            throw new BadRequestException("User id is null for deleting");
        }
        log.info("[{}]: Request to delete user with id - {}", UserServiceImpl.class, id);
        userRepository.deleteById(id);
        log.info("[{}]: User with id - {} was deleted", UserServiceImpl.class, id);
    }
}
