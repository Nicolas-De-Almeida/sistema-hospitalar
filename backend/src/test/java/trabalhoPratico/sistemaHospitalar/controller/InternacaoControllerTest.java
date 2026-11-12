package trabalhoPratico.sistemaHospitalar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import trabalhoPratico.sistemaHospitalar.dto.AltaInternacaoRequest;
import trabalhoPratico.sistemaHospitalar.dto.AtualizacaoInternacaoRequest;
import trabalhoPratico.sistemaHospitalar.dto.InternacaoRequest;
import trabalhoPratico.sistemaHospitalar.dto.InternacaoResponse;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusInternacao;
import trabalhoPratico.sistemaHospitalar.service.InternacaoService;

@WebMvcTest(InternacaoController.class)
class InternacaoControllerTest {

    private static final String JSON_CRIACAO = """
            {
              "pacienteId": 1,
              "profissionalId": 2,
              "quartoId": 3,
              "dataEntrada": "2026-11-11",
              "dataPrevistaAlta": "2026-11-16",
              "observacoes": "Observação inicial"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InternacaoService internacaoService;

    @Test
    void criacaoValidaRetorna201() throws Exception {
        when(internacaoService.criar(any(InternacaoRequest.class))).thenReturn(responseAtiva());

        mockMvc.perform(post("/api/internacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CRIACAO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/internacoes/10"))
                .andExpect(jsonPath("$.status").value("ATIVA"));
    }

    @Test
    void requisicaoInvalidaRetorna400() throws Exception {
        mockMvc.perform(post("/api/internacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void internacaoInexistenteRetorna404() throws Exception {
        when(internacaoService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Internação não encontrada: 99."));

        mockMvc.perform(get("/api/internacoes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void regraDeNegocioInvalidaRetorna422() throws Exception {
        when(internacaoService.criar(any(InternacaoRequest.class)))
                .thenThrow(new RegraDeNegocioException("O quarto está lotado."));

        mockMvc.perform(post("/api/internacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CRIACAO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem").value("O quarto está lotado."));
    }

    @Test
    void listaInternacoesFiltradasPorStatus() throws Exception {
        when(internacaoService.listar(StatusInternacao.ATIVA)).thenReturn(List.of(responseAtiva()));

        mockMvc.perform(get("/api/internacoes").param("status", "ATIVA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quartoNumero").value("101"));
    }

    @Test
    void atualizaInternacao() throws Exception {
        when(internacaoService.atualizar(any(Long.class), any(AtualizacaoInternacaoRequest.class)))
                .thenReturn(responseAtiva());

        mockMvc.perform(put("/api/internacoes/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "dataPrevistaAlta": "2026-11-20",
                                  "observacoes": "Nova observação"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void registraAlta() throws Exception {
        when(internacaoService.darAlta(any(Long.class), any(AltaInternacaoRequest.class)))
                .thenReturn(responseEncerrada());

        mockMvc.perform(patch("/api/internacoes/10/alta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dataAlta\":\"2026-11-12\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENCERRADA"))
                .andExpect(jsonPath("$.dataEfetivaAlta").value("2026-11-12"));
    }

    private static InternacaoResponse responseAtiva() {
        return new InternacaoResponse(
                10L, 1L, "Maria Silva", 2L, "Carlos Souza", 3L, "101",
                LocalDate.of(2026, 11, 11), LocalDate.of(2026, 11, 16), null,
                "Observação inicial", StatusInternacao.ATIVA);
    }

    private static InternacaoResponse responseEncerrada() {
        return new InternacaoResponse(
                10L, 1L, "Maria Silva", 2L, "Carlos Souza", 3L, "101",
                LocalDate.of(2026, 11, 11), LocalDate.of(2026, 11, 16),
                LocalDate.of(2026, 11, 12), "Observação inicial", StatusInternacao.ENCERRADA);
    }
}
