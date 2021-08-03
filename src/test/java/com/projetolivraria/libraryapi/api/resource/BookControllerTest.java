package com.projetolivraria.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetolivraria.libraryapi.api.dto.BookDTO;
import com.projetolivraria.libraryapi.api.exception.BusinessException;
import com.projetolivraria.libraryapi.model.entity.Book;
import com.projetolivraria.libraryapi.service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.swing.text.html.Option;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class}) // cria um minicontexto de injeção de dependencia
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class) // realiza os testes, faz o teste somente nessa pagina
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

    @Test
    @DisplayName("Deve obter informações de um livro")
    public void getBookDetailsTest() throws Exception {
        //cenario (given)
        Long id = 1l;
        Book book = Book.builder()
                .id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        //execucao (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()))
        ;



    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado não existir")
    public void bookNotFoundTest() throws Exception {
        // cenario
        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.empty());

        // execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() throws Exception {
        //cenario
        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.of(Book.builder().id(1l).build()));

        // execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1)); // para deletar a requisiçao não precisa de corpo

        mvc.perform(request)
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontrar o livro para deletar")
    public void deleteInexistentBookTest() throws Exception {
        //cenario
        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.empty());

        // execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1)); // para deletar a requisiçao não precisa de corpo

        mvc.perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() throws Exception {
        //cenario
        Long id = 1l;
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        Book updatingBook = Book.builder().id(1l).title("some title").author("some autor").isbn("321").build();
        BDDMockito.given(service.getById(id)) // procura o ID
                .willReturn(Optional.of(updatingBook)); // simula que retornou esse livro
        Book updatedBook = Book.builder().id(id).author("Cesar").title("As aventuras").isbn("321").build();
        BDDMockito.given(service.update(updatingBook)).willReturn(updatedBook);
        // execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value("321"));
        ;

    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente")
    public void updateInexistentBookTest() throws Exception {
        //cenario
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        BDDMockito.given(service.getById(Mockito.anyLong())) // procura o ID

                .willReturn(Optional.empty()); // simula que retornou esse livro

        // execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar livros")
    public void findBooksTest() throws Exception {
        Long id = 1l;

        Book book = Book.builder()
                .id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();

        BDDMockito.given( service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)) )
                .willReturn( new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(),
                book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0))
        ;
    }


    private BookDTO createNewBook() {
        return BookDTO.builder().author("Cesar").title("As aventuras").isbn("001").build();
    }

}
