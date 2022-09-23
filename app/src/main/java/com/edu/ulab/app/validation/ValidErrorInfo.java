package com.edu.ulab.app.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Информация о ошибке, описывающую ситуацию с валидацией
 * */

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ValidErrorInfo {

    /** Сообщение об ошибке */
    private String message;

    /** Статусный код ошибки */
    private HttpStatus code;

    /** Наименование исключения */
    private String exceptionName;

    /** Лист валидационных ошибок */
    private List<ValidError> validErrors;

}