package com.projetolivraria.libraryapi.service;


import com.projetolivraria.libraryapi.api.exception.BusinessException;
import com.projetolivraria.libraryapi.model.entity.Book;
import com.projetolivraria.libraryapi.model.repository.BookRepository;
import com.projetolivraria.libraryapi.service.impl.BookServiceImp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTeste {

    BookService service;

    @MockBean
    BookRepository repository;

    // vai ser executado antes de cada teste
    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImp( repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
//        cenario
        Book book = createValidBook();
        Mockito.when( repository.existsByIsbn(Mockito.anyString()) ).thenReturn(false);
        Mockito.when(repository.save(book))
                .thenReturn(Book.builder().id(1l)
                        .isbn("123")
                        .author("Fulano")
                        .title("As aventuras").build()
                );
        //        execucao
        Book savedBook = service.save(book);
//        verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
    }

    private Book createValidBook() {
        return Book.builder().isbn("123").author("FUlano").title("As aventuras").build();
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn ja cadastrado")
    public void shouldNotSaveABookWithDuplicatedISBN(){
        //cenario
        Book book = createValidBook();
        Mockito.when( repository.existsByIsbn(Mockito.anyString()) ).thenReturn(true);

        // execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        // verificaçoes
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isnb ja cadastrado");

    // não chama o metodo save
        Mockito.verify(repository, Mockito.never()).save(book);
    }

}
