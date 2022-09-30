package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        if(Objects.isNull(bookDto.getId())) {
            log.error("[{}]: Book id is null for updating {} ", BookServiceImpl.class, bookDto);
            throw new BadRequestException("Book id is null for updating");
        }
        log.info("[{}]: Request to update book - {}", BookServiceImpl.class, bookDto);

        Book book = bookMapper.bookDtoToBook(bookDto);

        log.info("[{}]: Mapped book: {}", BookServiceImpl.class, book);

        Book updatedBook = bookRepository.save(book);

        log.info("[{}]: Updated book: {}", BookServiceImpl.class, updatedBook);

        return bookMapper.bookToBookDto(updatedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        if(Objects.isNull(id)) {
            log.error("[{}]: Book id is null for getting", BookServiceImpl.class);
            throw new BadRequestException("Book id is null for getting");
        }

        log.info("[{}]: Request to get book with id - {}", BookServiceImpl.class, id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Book with id - %d was not found", id)));

        log.info("[{}]: Got book: {}", BookServiceImpl.class, book);

        return bookMapper.bookToBookDto(book);
    }

    @Override
    public void deleteBookById(Long id) {
        if(Objects.isNull(id)) {
            log.error("[{}]: Book id is null for deleting", BookServiceImpl.class);
            throw new BadRequestException("Book id is null for deleting");
        }
        log.info("[{}]: Request to delete book with id - {}", BookServiceImpl.class, id);
        bookRepository.deleteById(id);
        log.info("[{}]: Book with id - {} was deleted", BookServiceImpl.class, id);
    }

    @Override
    public List<BookDto> getAllByUserId(Long id) {
        if(Objects.isNull(id)) {
            log.error("[{}]: Book id is null for getting by user id", BookServiceImpl.class);
            throw new BadRequestException("Book id is null for getting by user id");
        }
        log.info("[{}]: Request to get book by user id {}", BookServiceImpl.class, id);
        List<Book> bookList = bookRepository.findAllByUserId(id);
        log.info("[{}]: Got book list - {}", BookServiceImpl.class, bookList);

        return bookList.stream()
                .map(bookMapper::bookToBookDto)
                .peek(mappedBook -> log.info("[{}]: Mapped book {}", BookServiceImpl.class, mappedBook))
                .collect(Collectors.toList());
    }
}
