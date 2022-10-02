package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.BadRequestException;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Тесты репозитория {@link BookRepository}.
 */

@SystemJpaTest
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить книгу и автора. Число insert должно равняться 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql"
    })
    void findAllBadges_thenAssertDmlCount() {
        //Given

        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author");
        book.setTitle("test");
        book.setPageCount(1000);
        book.setPerson(savedPerson);

        //When
        Book result = bookRepository.save(book);

        //Then
        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getTitle()).isEqualTo("test");
        assertSelectCount(0);
        assertInsertCount(2);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName("Обновить книгу. Должно быть 1 update и 1 select")
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    @Rollback
    void updateBook_Test() {

        Book book = bookRepository.findById(2002L).orElseGet(Book::new);
        book.setAuthor("Gogol");
        book.setTitle("Dead souls");
        book.setPageCount(650);

        Book result = bookRepository.saveAndFlush(book);


        assertThat(result.getPageCount()).isEqualTo(650);
        assertThat(result.getTitle()).isEqualTo("Dead souls");
        assertThat(result.getAuthor()).isEqualTo("Gogol");
        assertThat(result.getId()).isEqualTo(2002L);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(1);
        assertDeleteCount(0);
    }

    @DisplayName("Получение книги по id. Должен быть 1 select")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getBook_Test() {
        Book result = bookRepository.findById(4004L)
                .orElseGet(Book::new);

        assertThat(result.getPageCount()).isEqualTo(650);
        assertThat(result.getTitle()).isEqualTo("Dead souls");
        assertThat(result.getAuthor()).isEqualTo("Gogol");
        assertThat(result.getId()).isEqualTo(4004L);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);

    }

    @DisplayName("Получение лист книг по userId. Должен быть 1 select")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getBookList_Test() {
        List<Book> bookList = bookRepository.findAllByUserId(1001L);

        assertThat(bookList.size()).isEqualTo(3);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);

    }

    @DisplayName("Удаление книги по id. Должен быть 2 select and delete")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBook_Test() {
        bookRepository.deleteById(2002L);
        bookRepository.flush();
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(1);

    }

    @DisplayName("Получение книги по id которого не существует. Должен быть 1 select")
    @Test
    @Rollback
    void getBookNotExist_Test() {
        assertThat(bookRepository.findById(4004L)).isEqualTo(Optional.empty());


        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);

    }

    @DisplayName("Получение книги по id null. Должен быть exception")
    @Test
    @Rollback
    void getBookByNullId_Test() {
        assertThrows(Exception.class, () -> bookRepository.findById(null));
    }

    @Test
    @DisplayName("Создать null книгу. должен выйти exception")
    @Rollback
    void createBookNull_Test() {
        assertThrows(Exception.class, () -> bookRepository.save(null));
    }

    @Test
    @DisplayName("Создать книгу c null полями. должен выйти exception")
    @Rollback
    void createBookNullFields_Test() {
        Book book = new Book();
        book.setTitle("Clean code");
        assertThrows(Exception.class, () -> bookRepository.save(book));
    }


    @Test
    @DisplayName("Обновить книгу c person которого не существует. должен выйти exception")
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updateBookWithNoExistPerson_Test() {
        Person person = new Person();
        person.setId(1L);
        Book book = bookRepository.findById(4004L).orElseGet(Book::new);
        book.setPerson(person);
        assertThrows(Exception.class, () -> bookRepository.saveAndFlush(book));
    }

}
