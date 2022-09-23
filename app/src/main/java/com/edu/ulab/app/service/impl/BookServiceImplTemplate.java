package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

import static com.edu.ulab.app.web.constant.SQLQueryBookConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<BookDto> rowMapper = (resultSet, i) -> {
        Long id = resultSet.getLong("ID");
        String title = resultSet.getString("TITLE");
        String author = resultSet.getString("AUTHOR");
        long pageCount = resultSet.getLong("PAGE_COUNT");
        Long userId = resultSet.getLong("USER_ID");

        return BookDto.builder()
                .id(id)
                .author(author)
                .title(title)
                .pageCount(pageCount)
                .userId(userId)
                .build();
    };

    @Override
    public BookDto createBook(BookDto bookDto) {
        if(Objects.isNull(bookDto)) {
            log.error("[{}]: Book for saving is null", BookServiceImplTemplate.class);
            throw new BadRequestException(String.format("[%s] : Book for saving is null",
                    BookServiceImplTemplate.class));
        }
        log.info("[{}]: Request to save book: {}",BookServiceImplTemplate.class, bookDto);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, bookDto.getTitle());
                    ps.setString(2, bookDto.getAuthor());
                    ps.setLong(3, bookDto.getPageCount());
                    ps.setLong(4, bookDto.getUserId());
                    return ps;
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("[{}]: Saved book: {}",BookServiceImplTemplate.class, bookDto);
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {

        if(Objects.isNull(bookDto.getId())) {
            log.error("[{}]: Id for updating book is null", BookServiceImplTemplate.class);
            throw new BadRequestException(String.format("[%s] : Id for updating book is null",
                    BookServiceImplTemplate.class));
        }

        log.info("[{}]: Request to update book: {}",BookServiceImplTemplate.class, bookDto);
        jdbcTemplate.update(UPDATE_SQL, bookDto.getTitle(), bookDto.getAuthor(),
                bookDto.getPageCount(), bookDto.getAuthor(), bookDto.getId());
        log.info("[{}]: Updated book: {}",BookServiceImplTemplate.class, bookDto);
        return bookDto;
    }

    @Override
    public BookDto getBookById(Long id) {
        if(Objects.isNull(id)) {
            log.error("[{}]: Id for getting book is null", BookServiceImplTemplate.class);
            throw new BadRequestException(String.format("[%s] : Id for getting book is null",
                    BookServiceImplTemplate.class));
        }
        log.info("[{}]: Request to get book by id: {}",BookServiceImplTemplate.class, id);
        BookDto bookDto = jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, rowMapper, id);
        if(Objects.isNull(bookDto)) {
            log.error("[{}]: Book by id - {} was not found", BookServiceImplTemplate.class, id);
            throw new NotFoundException(String.format("[%s] : Book by id - %d was not found",
                    BookServiceImplTemplate.class, id));
        }
        log.info("[{}]: Got book: {}",BookServiceImplTemplate.class, bookDto);
        return bookDto;
    }

    @Override
    public void deleteBookById(Long id) {
        if(Objects.isNull(id)) {
            log.error("[{}]: Id for deleting book is null", BookServiceImplTemplate.class);
            throw new BadRequestException(String.format("[%s] : Id for deleting book is null",
                    BookServiceImplTemplate.class));
        }
        log.info("[{}]: Request to delete book by id: {}", BookServiceImplTemplate.class, id);
        int countOfDeletedBooks = jdbcTemplate.update(DELETE_BY_ID_SQL, id);
        log.info("[{}]: count of deleted books - {} with id - {} ",
                BookServiceImplTemplate.class, countOfDeletedBooks, id);
    }

    @Override
    public List<BookDto> getAllByUserId(Long id) {
        if(Objects.isNull(id)) {
            log.error("[{}]: Id for getting book by user id is null", BookServiceImplTemplate.class);
            throw new BadRequestException(String.format("[%s] : Id for getting book by user id is null",
                    BookServiceImplTemplate.class));
        }
        log.info("[{}]: Request to get books by user id: {}", BookServiceImpl.class, id);
        List<BookDto> bookDtoList = jdbcTemplate.query(SELECT_BY_USER_ID_SQL, rowMapper, id);
        log.info("[{}]: Got books by user id - {}", BookServiceImplTemplate.class, bookDtoList);
        return bookDtoList;
    }
}
