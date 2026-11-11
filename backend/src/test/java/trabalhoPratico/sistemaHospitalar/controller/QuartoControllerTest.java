package trabalhoPratico.sistemaHospitalar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import trabalhoPratico.sistemaHospitalar.dto.QuartoRequest;
import trabalhoPratico.sistemaHospitalar.dto.QuartoResponse;
import trabalhoPratico.sistemaHospitalar.exception.ConflitoException;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.model.enums.SituacaoQuarto;
import trabalhoPratico.sistemaHospitalar.model.enums.TipoQuarto;
import trabalhoPratico.sistemaHospitalar.service.QuartoService;

@WebMvcTest(QuartoController.class)
class QuartoControllerTest {

    private static final String JSON_VALIDO = """
            {
              "numero": "101",
              "andar": 1,
              "capacidadeMaxima": 2,
              "tipo": "APARTAMENTO"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuartoService quartoService;

    @Test
    void cadastroValidoRetorna201() throws Exception {
        when(quartoService.cadastrar(any(QuartoRequest.class))).thenReturn(responsePadrao());

        mockMvc.perform(post("/api/quartos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/quartos/1"))
                .andExpect(jsonPath("$.numero").value("101"));
    }

    @Test
    void requisicaoInvalidaRetorna400() throws Exception {
        mockMvc.perform(post("/api/quartos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void numeroDuplicadoRetorna409() throws Exception {
        when(quartoService.cadastrar(any(QuartoRequest.class)))
                .thenThrow(new ConflitoException("Número duplicado."));

        mockMvc.perform(post("/api/quartos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("Número duplicado."));
    }

    @Test
    void quartoInexistenteRetorna404() throws Exception {
        when(quartoService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Quarto não encontrado: 99."));

        mockMvc.perform(get("/api/quartos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listaQuartosDisponiveis() throws Exception {
        when(quartoService.listarDisponiveis()).thenReturn(List.of(responsePadrao()));

        mockMvc.perform(get("/api/quartos/disponiveis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].vagasDisponiveis").value(2));
    }

    @Test
    void atualizaQuarto() throws Exception {
        when(quartoService.atualizar(any(Long.class), any(QuartoRequest.class)))
                .thenReturn(responsePadrao());

        mockMvc.perform(put("/api/quartos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("APARTAMENTO"));
    }

    private static QuartoResponse responsePadrao() {
        return new QuartoResponse(
                1L, "101", 1, 2, TipoQuarto.APARTAMENTO,
                0, 2, SituacaoQuarto.DISPONIVEL);
    }
}
