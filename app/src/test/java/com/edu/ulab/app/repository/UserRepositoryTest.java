package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Тесты репозитория {@link UserRepository}.
 */
@SystemJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить юзера. Число insert должно равняться 1")
    @Test
    @Rollback
    void insertPerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        //When
        Person result = userRepository.save(person);

        //Then
        assertThat(result.getAge()).isEqualTo(111);
        assertSelectCount(0);
        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Обновить юзера. Число update и select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updatePerson_thenAssertDmlCount() {
        //Given
        Person person = userRepository.findById(1001L).orElseGet(Person::new);
        person.setTitle("tester");
        person.setFullName("Vova");
        person.setAge(20);
        //When
        Person result = userRepository.saveAndFlush(person);

        //Then
        assertThat(result.getAge()).isEqualTo(20);
        assertThat(result.getTitle()).isEqualTo("tester");
        assertThat(result.getFullName()).isEqualTo("Vova");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(1);
        assertDeleteCount(0);
    }


    @DisplayName("Получение юзера по id. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getPerson_thenAssertDmlCount() {
        //Given

        Person result = userRepository.findById(6006L).orElseGet(Person::new);

        //Then
        assertThat(result.getAge()).isEqualTo(22);
        assertThat(result.getTitle()).isEqualTo("student");
        assertThat(result.getFullName()).isEqualTo("Ivan");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получение юзера по id, которого нет в БД. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getPersonByNotExistId_thenAssertDmlCount() {
        assertThat(userRepository.findById(7006L)).isEqualTo(Optional.empty());
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получение юзера по id - null. должно выйти exception")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getPersonByNullId_Test() {
        assertThrows(Exception.class, () -> userRepository.findById(null));
    }


    @DisplayName("Удаление юзера по id. Число select должно равняться 2 и delete - 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePerson_thenAssertDmlCount() {

        userRepository.deleteById(6006L);
        userRepository.flush();
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @DisplayName("Удаление юзера по id - которого нет. Число select должно равняться 1, должен выбросить exception")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePersonByNotExistId_thenAssertDmlCount() {
        assertThrows(Exception.class,() ->  userRepository.deleteById(7006L));
    }

    @DisplayName("Удаление юзера по id , у которого есть связи с книгами. Число select должно равняться 2, число delete - 4 ")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePersonWithBooks_thenAssertDmlCount() {
        userRepository.deleteById(1001L);
        userRepository.flush();
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(4);
    }


    @DisplayName("Получение всех user . Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getAllPerson_thenAssertDmlCount() {
        List<Person> personList = userRepository.findAll();
        assertThat(personList.size()).isEqualTo(2);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Сохранить юзера с not unique title. должен кинуть exception")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void insertPersonWithNoUniqueTitle_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("student");
        person.setFullName("Test Test");

        assertThrows(Exception.class, () -> userRepository.save(person));

    }
}
