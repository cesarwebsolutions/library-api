package com.projetolivraria.libraryapi.api.dto;

import javax.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Builder // gera um builder para a classe alvo
@NoArgsConstructor // para corrigir o erro causado pela import acima
@AllArgsConstructor
//Classe simples somente para respresentar as informações JSOn
public class BookDTO {
    private Long id;

    @NotEmpty
    private String title;
    @NotEmpty
    private String author;
    @NotEmpty
    private String isbn;


}
