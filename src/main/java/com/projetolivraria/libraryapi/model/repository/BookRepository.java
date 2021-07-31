package com.projetolivraria.libraryapi.model.repository;

import com.projetolivraria.libraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn); //primeiro é a entidade e o outro é o tipo da chave primaria
}
