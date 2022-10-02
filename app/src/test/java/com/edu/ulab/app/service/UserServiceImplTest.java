package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void savePerson_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person  = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person savedPerson  = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");


        //when

        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);


        //then

        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(1L, userDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно.")
    void updatePerson_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person  = new Person();
        person.setId(1L);
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person updatedPerson  = new Person();
        updatedPerson.setId(1L);
        updatedPerson.setFullName("test name");
        updatedPerson.setAge(11);
        updatedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");


        //when

        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(updatedPerson);
        when(userMapper.personToUserDto(updatedPerson)).thenReturn(result);


        //then

        UserDto userDtoResult = userService.updateUser(userDto);
        assertEquals(1L, userDtoResult.getId());
        assertEquals(11, userDtoResult.getAge());
        assertEquals("test name", userDtoResult.getFullName());
        assertEquals("test title", userDtoResult.getTitle());

    }

    @Test
    @DisplayName("Обновление пользователя без id. Должно вылететь ошибка.")
    void updateUserWithNoId_Test() {

        UserDto userDto = UserDto.builder()
                .age(13)
                .fullName("Marsel")
                .title("student")
                .build();



        assertThrows(BadRequestException.class, () -> userService.updateUser(userDto));
    }


    @Test
    @DisplayName("Получение пользователя по id. Должны пройти успешно.")
    void getUser_Test() {

        Long userId = 2L;

        UserDto resultUserDto = UserDto.builder()
                .id(userId)
                .age(13)
                .fullName("Marsel")
                .title("student")
                .build();

        Person person = new Person();
        person.setId(userId);
        person.setAge(13);
        person.setFullName("Marsel");
        person.setTitle("student");

        when(userRepository.findById(userId)).thenReturn(Optional.of(person));
        when(userMapper.personToUserDto(person)).thenReturn(resultUserDto);

        UserDto givenUser = userService.getUserById(userId);
        assertEquals(userId, givenUser.getId());
        assertEquals(13, givenUser.getAge());
        assertEquals("Marsel", givenUser.getFullName());
        assertEquals("student", givenUser.getTitle());
    }


    @Test
    @DisplayName("Получение пользователя по id которого нет. Должно выкинуть exception.")
    void getBookWithNoExistId_Test() {

        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(2L));
    }

    @Test
    @DisplayName("Получение пользователя по id = null. Должно выкинуть exception.")
    void getBookWithNullId_Test() {
        assertThrows(BadRequestException.class, () -> userService.getUserById(null));
    }

    @Test
    @DisplayName("Удаление пользователя по id = null. Должно выкинуть exception.")
    void deleteBookWithNullId_Test() {
        assertThrows(BadRequestException.class, () -> userService.deleteUserById(null));
    }

    @Test
    @DisplayName("Удаление пользователя по id. Должно пройти успешно.")
    void deleteBookWithId_Test() {
        assertDoesNotThrow(() -> userService.deleteUserById(1L));
    }
}
