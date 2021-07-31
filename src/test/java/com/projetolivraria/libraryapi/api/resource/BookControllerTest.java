package com.projetolivraria.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetolivraria.libraryapi.api.dto.BookDTO;
import com.projetolivraria.libraryapi.api.exception.BusinessException;
import com.projetolivraria.libraryapi.model.entity.Book;
import com.projetolivraria.libraryapi.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class}) // cria um minicontexto de injeção de dependencia
@ActiveProfiles("test")
@WebMvcTest // realiza os testes
@AutoConfigureMockMvc // auto importa o objeto de teste
public class BookControllerTest {

//    definir rota
    static String BOOK_API = "/api/books";

    @Autowired //injeta um objeto
    MockMvc mvc; // objeto que moca os teste

    @MockBean // estancia para crias o mock
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void  createBookTest() throws Exception {
         BookDTO dto = createNewBook();   // tranforma em JSOn e manda pra a requisição
        Book savedBook = Book.builder().id(10l).author("Cesar").title("As aventuras").isbn("001").build();
        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
        // recebe um objeto de qualquer tipo e transforma em JSON
        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // fazendo a requisição
        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()))
        ;
    }



    @Test
    @DisplayName("Deve lançar erro se não houver dados suficientes para criação do livro")
    public void  createInvalidBookTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastar um livro com isbn ja utilizado")
    public void createBookwithDuplicatedIsbn() throws Exception {

        BookDTO dto = createNewBook();
        String json = new ObjectMapper().writeValueAsString(dto);
        String mensagemErro = "Isnb ja cadastrado";
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(mensagemErro));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform( request )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(mensagemErro));
    }




    private BookDTO createNewBook() {
        return BookDTO.builder().author("Cesar").title("As aventuras").isbn("001").build();
    }

}
