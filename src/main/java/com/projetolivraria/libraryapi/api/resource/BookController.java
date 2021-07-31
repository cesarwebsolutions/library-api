package com.projetolivraria.libraryapi.api.resource;
import com.projetolivraria.libraryapi.api.dto.BookDTO;
import com.projetolivraria.libraryapi.api.exception.ApiErros;
import com.projetolivraria.libraryapi.api.exception.BusinessException;
import com.projetolivraria.libraryapi.model.entity.Book;

import com.projetolivraria.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books") // cria a rota
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper mapper) {
        this.service = service;
        this.modelMapper = mapper;
    }

    @PostMapping // diz que vai tratar o bloco para a rota
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        Book entity = modelMapper.map(dto, Book.class);
        entity = service.save(entity);
        return modelMapper.map(entity, BookDTO.class);
    }

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

}
