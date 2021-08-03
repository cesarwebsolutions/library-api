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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("Deve obter um livro por ID")
    public void getByIdTest() {
        Long id = 1l;
        Book book = createValidBook();// cria o book
        book.setId(id); // seta o ID
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        // execucao
        Optional<Book> foundBook = service.getById(id);

        // verificaçoes
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    @DisplayName("deve retornar vazio ao obter um livro por id quando ele não existe na base")
    public void bookNotFoundIdTest() {
        Long id = 1l;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        // execucao
        Optional<Book> book = service.getById(id);

        // verificaçoes
        assertThat(book.isPresent()).isFalse();

    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTeste() {
        //cenario
        Book book = Book.builder().id(1l).build();

        // execucao
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));

        //verificacao
        Mockito.verify(repository, Mockito.times(1)).delete(book);

    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente")
    public void deleteInvalidBookTest() {
        // cenario
        Book book = new Book();

        //execucao
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->service.delete(book));

        //verificacao
        Mockito.verify(repository, Mockito.never()).delete(book);


    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente")
    public void updateInvalidBookTest() {
        // cenario
        Book book = new Book();

        //execucao
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->service.update(book));

        //verificacao
        Mockito.verify(repository, Mockito.never()).save(book);


    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTeste() {
        //cenario
        long id = 1l;

        //livro a atualizar
        Book updatingBook = Book.builder().id(id).build();

        // simulacao
        Book updateBook = createValidBook();
        updateBook.setId(id);
        Mockito.when(repository.save(updatingBook)).thenReturn(updateBook);

        //execucao
        Book book = service.update(updatingBook);

        //verificacao
        assertThat(book.getId()).isEqualTo(updateBook.getId());
        assertThat(book.getTitle()).isEqualTo(updateBook.getTitle());
        assertThat(book.getIsbn()).isEqualTo(updateBook.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(updateBook.getAuthor());

    }

    @Test
    @DisplayName("Deve Filtrar livros pelas propriedades")
    public void findBookTest() {
        //cenario
        Book book = createValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(lista, pageRequest, 1);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);
        // execucao
        Page<Book> result = service.find(book, pageRequest);

        //verificacoes
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

}
