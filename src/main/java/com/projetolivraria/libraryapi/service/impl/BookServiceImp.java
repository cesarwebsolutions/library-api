package com.projetolivraria.libraryapi.service.impl;

import com.projetolivraria.libraryapi.api.exception.BusinessException;
import com.projetolivraria.libraryapi.model.entity.Book;
import com.projetolivraria.libraryapi.model.repository.BookRepository;
import com.projetolivraria.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

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
}
