package trabalhoPratico.sistemaHospitalar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import trabalhoPratico.sistemaHospitalar.dto.ConclusaoProcedimentoRequest;
import trabalhoPratico.sistemaHospitalar.dto.ProcedimentoCirurgicoRequest;
import trabalhoPratico.sistemaHospitalar.dto.ProcedimentoCirurgicoResponse;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusProcedimento;
import trabalhoPratico.sistemaHospitalar.model.enums.TipoCirurgia;
import trabalhoPratico.sistemaHospitalar.service.ProcedimentoCirurgicoService;

@WebMvcTest(ProcedimentoCirurgicoController.class)
class ProcedimentoCirurgicoControllerTest {

    private static final String JSON_AGENDAMENTO = """
            {
              "pacienteId": 1,
              "cirurgiaoId": 2,
              "dataPrevista": "2099-01-05",
              "horarioPrevisto": "09:00",
              "descricao": "Cirurgia cardíaca",
              "tipoCirurgia": "ELETIVA",
              "observacoesPreOperatorias": "Jejum de oito horas"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcedimentoCirurgicoService procedimentoService;

    @Test
    void agendaProcedimentoRetornando201() throws Exception {
        when(procedimentoService.agendar(any(ProcedimentoCirurgicoRequest.class)))
                .thenReturn(response(StatusProcedimento.AGENDADO));

        mockMvc.perform(post("/api/procedimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_AGENDAMENTO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/procedimentos/20"))
                .andExpect(jsonPath("$.status").value("AGENDADO"));
    }

    @Test
    void dadosInvalidosRetornam400() throws Exception {
        mockMvc.perform(post("/api/procedimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listaProcedimentosPorStatus() throws Exception {
        when(procedimentoService.listar(StatusProcedimento.AGENDADO))
                .thenReturn(List.of(response(StatusProcedimento.AGENDADO)));

        mockMvc.perform(get("/api/procedimentos").param("status", "AGENDADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoCirurgia").value("ELETIVA"));
    }

    @Test
    void iniciaProcedimento() throws Exception {
        when(procedimentoService.iniciar(20L)).thenReturn(response(StatusProcedimento.EM_ANDAMENTO));

        mockMvc.perform(patch("/api/procedimentos/20/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
    }

    @Test
    void concluiProcedimento() throws Exception {
        when(procedimentoService.concluir(any(Long.class), any(ConclusaoProcedimentoRequest.class)))
                .thenReturn(response(StatusProcedimento.CONCLUIDO));

        mockMvc.perform(patch("/api/procedimentos/20/concluir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"observacoesPosOperatorias\":\"Sem intercorrências.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDO"));
    }

    @Test
    void regraDeNegocioInvalidaRetorna422() throws Exception {
        when(procedimentoService.concluir(any(Long.class), any(ConclusaoProcedimentoRequest.class)))
                .thenThrow(new RegraDeNegocioException("O procedimento ainda não foi iniciado."));

        mockMvc.perform(patch("/api/procedimentos/20/concluir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void cancelaProcedimento() throws Exception {
        when(procedimentoService.cancelar(20L)).thenReturn(response(StatusProcedimento.CANCELADO));

        mockMvc.perform(patch("/api/procedimentos/20/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"));
    }

    private static ProcedimentoCirurgicoResponse response(StatusProcedimento status) {
        boolean iniciado = status == StatusProcedimento.EM_ANDAMENTO || status == StatusProcedimento.CONCLUIDO;
        return new ProcedimentoCirurgicoResponse(
                20L, 1L, "Maria Silva", 2L, "Paulo Rocha",
                LocalDate.of(2099, 1, 5), LocalTime.of(9, 0),
                iniciado ? LocalDate.of(2099, 1, 5) : null,
                iniciado ? LocalTime.of(9, 5) : null,
                "Cirurgia cardíaca", TipoCirurgia.ELETIVA, "Jejum de oito horas",
                status == StatusProcedimento.CONCLUIDO ? "Sem intercorrências." : null,
                status, status.getDescricao());
    }
}
