package com.projetolivraria.libraryapi.model.repository;


import com.projetolivraria.libraryapi.model.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest // faz uso do banco em memoria para fazer os teste de integração (istancia h2)
public class BookRepositoryTeste {

    @Autowired
    TestEntityManager entityManager; // está frente ao banco de dados realizando tudo

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com isbn informado")
    public void returnTrueWhenIsbnExists() {
        // cenario
        String isbn = "123";
        Book book = Book.builder().title("Aventuras").author("Cesar").isbn(isbn).build();
        entityManager.persist(book);

        // execucao
        boolean exists = repository.existsByIsbn(isbn);

        //verificacao
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando nao existir um livro na base com isbn informado")
    public void returnFalseWhenIsbnDoentExist() {
        // cenario
        String isbn = "123";


        // execucao
        boolean exists = repository.existsByIsbn(isbn);

        //verificacao
        assertThat(exists).isFalse();
    }
}
