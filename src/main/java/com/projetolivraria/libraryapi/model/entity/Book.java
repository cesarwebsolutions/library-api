package com.projetolivraria.libraryapi.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data // subistitui o @gatter e @satter
@Builder
@AllArgsConstructor // com o @builder é necessario adicionar essa classe
@NoArgsConstructor
@Entity // diz para a classe que ela é uma entidade
@Table

public class Book {
    // com isso entendese que no banco de dados tem uma tabela com o nome Book e as colunas

    @Id // para mapear pelo id
    @Column //(name = "") quando for usar uma coluna diferente do ID
    @GeneratedValue(strategy = GenerationType.IDENTITY) // diz que o ID na base de dados é auto incremento, e o banco que vais e encarregar de gerar
    private Long id;
    @Column
    private String title;
    @Column
    private String author;
    @Column
    private String isbn;
}
