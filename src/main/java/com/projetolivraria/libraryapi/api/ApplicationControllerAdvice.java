package com.projetolivraria.libraryapi.api;

import com.projetolivraria.libraryapi.api.exception.ApiErros;
import com.projetolivraria.libraryapi.api.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice // diz que vai ter aplicações globais, para toda a api
public class ApplicationControllerAdvice {
    // lida com o erro de validação
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErros handValidationExceptions(MethodArgumentNotValidException exception){
        BindingResult bindingResult = exception.getBindingResult();
        return new ApiErros(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErros handleBusinessException(BusinessException exception) {
        return new ApiErros(exception);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity handleResponseStatusException(ResponseStatusException exception) {
        return new ResponseEntity(new ApiErros(exception), exception.getStatus());
    }
}
