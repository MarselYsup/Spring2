package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.Objects;

import static com.edu.ulab.app.web.constant.SQLQueryUserConstant.DELETE_BY_ID_SQL;
import static com.edu.ulab.app.web.constant.SQLQueryUserConstant.SELECT_BY_ID_SQL;
import static com.edu.ulab.app.web.constant.SQLQueryUserConstant.INSERT_SQL;
import static com.edu.ulab.app.web.constant.SQLQueryUserConstant.UPDATE_SQL;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplTemplate implements UserService {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<UserDto> rowMapper = (resultSet, i) -> {
      Long id = resultSet.getLong("ID");
      String title = resultSet.getString("TITLE");
      String fullName = resultSet.getString("FULL_NAME");
      int age = resultSet.getInt("AGE");

      return UserDto.builder()
              .id(id)
              .title(title)
              .fullName(fullName)
              .age(age)
              .build();
    };

    @Override
    public UserDto createUser(UserDto userDto) {

        if(Objects.isNull(userDto)) {
            log.error("[{}]: User for saving is null", UserServiceImplTemplate.class);
            throw new BadRequestException(String.format("[%s] : User for saving is null",
                    UserServiceImplTemplate.class));
        }
        log.info("[{}]: Request to save user: {}", UserServiceImplTemplate.class, userDto);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("[{}]: Saved book: {}", UserServiceImplTemplate.class, userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        if(Objects.isNull(userDto.getId())) {
            log.error("[{}]: Id for updating user is null", UserServiceImplTemplate.class);
            throw new BadRequestException(String.format("[%s] : Id for updating user is null",
                    UserServiceImplTemplate.class));
        }

        log.info("[{}]: Request to update user: {}", UserServiceImplTemplate.class, userDto);
        jdbcTemplate.update(UPDATE_SQL, userDto.getFullName(), userDto.getTitle(), userDto.getAge(), userDto.getId());
        log.info("[{}]: Updated user: {}",UserServiceImplTemplate.class, userDto);
        return userDto;
    }

    @Override
    public UserDto getUserById(Long id) {
        if(Objects.isNull(id)) {
            log.error("[{}]: Id for getting user is null", UserServiceImplTemplate.class);
            throw new BadRequestException(String.format("[%s] : Id for getting user is null",
                    UserServiceImplTemplate.class));
        }
        log.info("[{}]: Request to get user by id: {}", UserServiceImplTemplate.class, id);
        UserDto userDto = null;
        try {
            userDto = jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, rowMapper, id);
        }catch (DataAccessException ex) {
            log.error("[{}]: User was not found with id - {}", UserServiceImplTemplate.class, id);
            throw new NotFoundException(String.format("User was not found with id - %d", id));
        }

        log.info("[{}]: Got book: {}", UserServiceImplTemplate.class, userDto);
        return userDto;
    }

    @Override
    public void deleteUserById(Long id) {
        if(Objects.isNull(id)) {
            log.error("[{}]: Id for deleting user is null", UserServiceImplTemplate.class);
            throw new BadRequestException(String.format("[%s] : Id for deleting user is null",
                    UserServiceImplTemplate.class));
        }
        log.info("[{}]: Request to delete user by id: {}", UserServiceImplTemplate.class, id);
        int countOfDeletedUsers = jdbcTemplate.update(DELETE_BY_ID_SQL, id);
        log.info("[{}]: count of deleted users - {} with id - {} ",
                UserServiceImplTemplate.class, countOfDeletedUsers, id);
    }
}
