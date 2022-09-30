package com.edu.ulab.app.web.handler;


import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.validation.ValidError;
import com.edu.ulab.app.validation.ValidErrorInfo;
import com.edu.ulab.app.web.response.BaseWebResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidErrorInfo> handleValidException(MethodArgumentNotValidException e) {

        ValidErrorInfo errorValidDto = ValidErrorInfo.builder()
                .code(HttpStatus.BAD_REQUEST)
                .message("Validation error")
                .validErrors(processFieldErrors(e.getBindingResult()))
                .exceptionName(e.getClass().getSimpleName())
                .build();

        return new ResponseEntity<>(errorValidDto, errorValidDto.getCode());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BaseWebResponse> handleNotFoundExceptionException(@NonNull final NotFoundException exc) {
        log.error(exc.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new BaseWebResponse(createErrorMessage(exc)));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseWebResponse> handleNotFoundExceptionException(@NonNull final BadRequestException exc) {
        log.error(exc.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseWebResponse(createErrorMessage(exc)));
    }

    private String createErrorMessage(Exception exception) {
        final String message = exception.getMessage();
        log.error(ExceptionHandlerUtils.buildErrorMessage(exception));
        return message;
    }

    private List<ValidError> processFieldErrors(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map( e -> {
                    if (e instanceof FieldError) {
                        FieldError error = (FieldError) e;
                        return new ValidError(error.getField(),error.getDefaultMessage());
                    }
                    return new ValidError(e.getObjectName(), e.getDefaultMessage());
                })
                .collect(Collectors.toList());

    }
}
