package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void saveBook_Test() {
        //given
        Person person  = new Person();
        person.setId(1L);

        BookDto bookDto = BookDto.builder()
                .userId(1L)
                .author("test author")
                .title("test titile")
                .pageCount(1000)
                .build();

        BookDto result = BookDto.builder()
                .id(1L)
                .userId(1L)
                .author("test author")
                .title("test title")
                .pageCount(1000)
                .build();

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);


        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление книги. Должно пройти успешно.")
    void updateBook_Test() {
        //given
        Person person  = new Person();
        person.setId(1L);

        BookDto bookDto = BookDto.builder()
                .id(1L)
                .userId(1L)
                .author("test author 2")
                .title("test titile 2")
                .pageCount(500)
                .build();

        BookDto result = BookDto.builder()
                .id(1L)
                .userId(1L)
                .author("test author 2")
                .title("test titile 2")
                .pageCount(500)
                .build();

        Book book = new Book();
        book.setPageCount(500);
        book.setTitle("test titile 2");
        book.setAuthor("test author 2");
        book.setPerson(person);

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setPageCount(500);
        updatedBook.setTitle("test titile 2");
        updatedBook.setAuthor("test author 2");
        updatedBook.setPerson(person);

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(updatedBook);
        when(bookMapper.bookToBookDto(updatedBook)).thenReturn(result);


        //then
        BookDto bookDtoResult = bookService.updateBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
        assertEquals("test titile 2", bookDtoResult.getTitle());
        assertEquals("test author 2", bookDtoResult.getAuthor());
        assertEquals(500, bookDtoResult.getPageCount());
    }

    @Test
    @DisplayName("Обновление книги без id. Должно вылететь ошибка.")
    void updateBookWithNoId_Test() {

        BookDto bookDto = BookDto.builder()
                .userId(1L)
                .author("test author 2")
                .title("test titile 2")
                .pageCount(500)
                .build();



        assertThrows(BadRequestException.class, () -> bookService.updateBook(bookDto));
    }

    @Test
    @DisplayName("Получение книги. Должно пройти успешно.")
    void getBook_Test() {
        //given
        Person person  = new Person();
        person.setId(2L);

        BookDto result = BookDto.builder()
                .id(1L)
                .userId(1L)
                .author("test author 2")
                .title("test titile 2")
                .pageCount(500)
                .build();

        Book givenBook = new Book();
        givenBook.setId(1L);
        givenBook.setPageCount(500);
        givenBook.setTitle("test titile 2");
        givenBook.setAuthor("test author 2");
        givenBook.setPerson(person);

        //when


        when(bookRepository.findById(1L)).thenReturn(Optional.of(givenBook));
        when(bookMapper.bookToBookDto(givenBook)).thenReturn(result);


        //then
        BookDto bookDtoResult = bookService.getBookById(1L);
        assertEquals(1L, bookDtoResult.getId());
        assertEquals("test titile 2", bookDtoResult.getTitle());
        assertEquals("test author 2", bookDtoResult.getAuthor());
        assertEquals(500, bookDtoResult.getPageCount());
    }


    @Test
    @DisplayName("Получение книги по id которого нет. Должно выкинуть exception.")
    void getBookWithNoExistId_Test() {

        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.getBookById(2L));
    }

    @Test
    @DisplayName("Получение книги по id = null. Должно выкинуть exception.")
    void getBookWithNullId_Test() {
        assertThrows(BadRequestException.class, () -> bookService.getBookById(null));
    }

    @Test
    @DisplayName("Получение всех книг по user id. Должно пройти успешно")
    void getBooksByUserId_Test() {
        //given
        Long userId = 2L;
        Person person = new Person();
        person.setId(userId);

        Book givenBook1 = new Book();
        givenBook1.setId(1L);
        givenBook1.setPageCount(500);
        givenBook1.setTitle("test titile 2");
        givenBook1.setAuthor("test author 2");
        givenBook1.setPerson(person);

        BookDto bookDto1 = BookDto.builder()
                .id(1L)
                .author("test author 2")
                .title("test titile 2")
                .pageCount(500)
                .userId(userId)
                .build();


        Book givenBook2 = new Book();
        givenBook2.setId(2L);
        givenBook2.setPageCount(600);
        givenBook2.setTitle("test titile 3");
        givenBook2.setAuthor("test author 3");
        givenBook2.setPerson(person);

        BookDto bookDto2 = BookDto.builder()
                .id(2L)
                .author("test author 3")
                .title("test titile 3")
                .pageCount(600)
                .userId(userId)
                .build();

        Book givenBook3 = new Book();
        givenBook3.setId(3L);
        givenBook3.setPageCount(400);
        givenBook3.setTitle("test titile 4");
        givenBook3.setAuthor("test author 4");
        givenBook3.setPerson(person);

        BookDto bookDto3 = BookDto.builder()
                .id(3L)
                .author("test author 4")
                .title("test titile 4")
                .pageCount(400)
                .userId(userId)
                .build();

        List<Book> books = new ArrayList<>();
        books.add(givenBook1);
        books.add(givenBook2);
        books.add(givenBook3);

        List<BookDto> resultBooks = new ArrayList<>();
        resultBooks.add(bookDto1);
        resultBooks.add(bookDto2);
        resultBooks.add(bookDto3);

        when(bookRepository.findAllByUserId(userId)).thenReturn(books);
        when(bookMapper.bookToBookDto(givenBook1)).thenReturn(bookDto1);
        when(bookMapper.bookToBookDto(givenBook2)).thenReturn(bookDto2);
        when(bookMapper.bookToBookDto(givenBook3)).thenReturn(bookDto3);

        List<BookDto> result = bookService.getAllByUserId(userId);
        assertEquals(resultBooks, result);
    }

    @Test
    @DisplayName("Получение всех книг по user id, которого нет. Должно пройти успешно, выйти пустой лист")
    void getBooksWithNoExistId_Test() {
        when(bookRepository.findAllByUserId(1L)).thenReturn(new ArrayList<>());
        assertEquals(bookService.getAllByUserId(1L), new ArrayList<>());
    }


    @Test
    @DisplayName("Получение всех книг по user id - null. Должно выйти ошибка")
    void getBooksWithNullId_Test() {
        assertThrows(BadRequestException.class, () -> bookService.getAllByUserId(null));
    }

    @Test
    @DisplayName("Удаление книги по id = null. Должно выкинуть exception.")
    void deleteBookWithNullId_Test() {
        assertThrows(BadRequestException.class, () -> bookService.deleteBookById(null));
    }

    @Test
    @DisplayName("Удаление книги по id. Должно пройти успешно.")
    void deleteBookWithId_Test() {
       assertDoesNotThrow(() -> bookService.deleteBookById(1L));
    }

}
