package com.projetolivraria.libraryapi.service.impl;

import com.projetolivraria.libraryapi.api.exception.BusinessException;
import com.projetolivraria.libraryapi.model.entity.Book;
import com.projetolivraria.libraryapi.model.repository.BookRepository;
import com.projetolivraria.libraryapi.service.BookService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImp implements BookService {
    private BookRepository repository;

    public BookServiceImp(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isnb ja cadastrado");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Book id cant be null");
        }
        this.repository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Book id cant be null");
        }
        return this.repository.save(book);
    }

    @Override
    public Page<Book> find(Book filter, Pageable pageRequest) {
        Example<Book> example = Example.of(filter,
                ExampleMatcher
                    .matching()
                    .withIgnoreCase()// consegue pesquisar tanto em caixa alta ou caixa baixa
                    .withIgnoreNullValues()// ignora se tiver pesquisa nula
                    .withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING) // traz a pesquisa mesmo que o titulo nao esteje completo

        );
        return repository.findAll(example, pageRequest);
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        return null;
    }
}
