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

import trabalhoPratico.sistemaHospitalar.dto.ConclusaoConsultaRequest;
import trabalhoPratico.sistemaHospitalar.dto.ConsultaRequest;
import trabalhoPratico.sistemaHospitalar.dto.ConsultaResponse;
import trabalhoPratico.sistemaHospitalar.dto.RemarcacaoConsultaRequest;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.enums.ModalidadeConsulta;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusConsulta;
import trabalhoPratico.sistemaHospitalar.service.ConsultaService;

@WebMvcTest(ConsultaController.class)
class ConsultaControllerTest {

    private static final String JSON_AGENDAMENTO = """
            {
              "pacienteId": 1,
              "profissionalId": 2,
              "modalidade": "ROTINA",
              "data": "2099-01-05",
              "horario": "09:00",
              "motivo": "Avaliação de rotina"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultaService consultaService;

    @Test
    void agendamentoValidoRetorna201() throws Exception {
        when(consultaService.agendar(any(ConsultaRequest.class)))
                .thenReturn(response(StatusConsulta.AGENDADA));

        mockMvc.perform(post("/api/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_AGENDAMENTO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/consultas/10"))
                .andExpect(jsonPath("$.status").value("AGENDADA"));
    }

    @Test
    void requisicaoInvalidaRetorna400() throws Exception {
        mockMvc.perform(post("/api/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void consultaInexistenteRetorna404() throws Exception {
        when(consultaService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Consulta não encontrada: 99."));

        mockMvc.perform(get("/api/consultas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void conflitoDeAgendaRetorna422() throws Exception {
        when(consultaService.agendar(any(ConsultaRequest.class)))
                .thenThrow(new RegraDeNegocioException("Horário indisponível."));

        mockMvc.perform(post("/api/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_AGENDAMENTO))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void listaPorStatus() throws Exception {
        when(consultaService.listar(StatusConsulta.AGENDADA))
                .thenReturn(List.of(response(StatusConsulta.AGENDADA)));

        mockMvc.perform(get("/api/consultas").param("status", "AGENDADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].modalidade").value("ROTINA"));
    }

    @Test
    void confirmaConsulta() throws Exception {
        when(consultaService.confirmar(10L)).thenReturn(response(StatusConsulta.CONFIRMADA));

        mockMvc.perform(patch("/api/consultas/10/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMADA"));
    }

    @Test
    void iniciaConsulta() throws Exception {
        when(consultaService.iniciar(10L)).thenReturn(response(StatusConsulta.EM_ATENDIMENTO));

        mockMvc.perform(patch("/api/consultas/10/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ATENDIMENTO"));
    }

    @Test
    void concluiConsultaComObservacoes() throws Exception {
        when(consultaService.concluir(any(Long.class), any(ConclusaoConsultaRequest.class)))
                .thenReturn(response(StatusConsulta.CONCLUIDA));

        mockMvc.perform(patch("/api/consultas/10/concluir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"observacoes\":\"Paciente avaliado.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));
    }

    @Test
    void cancelaConsulta() throws Exception {
        when(consultaService.cancelar(10L)).thenReturn(response(StatusConsulta.CANCELADA));

        mockMvc.perform(patch("/api/consultas/10/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));
    }

    @Test
    void registraFalta() throws Exception {
        when(consultaService.registrarFalta(10L)).thenReturn(response(StatusConsulta.NAO_COMPARECEU));

        mockMvc.perform(patch("/api/consultas/10/falta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NAO_COMPARECEU"));
    }

    @Test
    void remarcaConsulta() throws Exception {
        when(consultaService.remarcar(any(Long.class), any(RemarcacaoConsultaRequest.class)))
                .thenReturn(response(StatusConsulta.AGENDADA));

        mockMvc.perform(patch("/api/consultas/10/remarcar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"data\":\"2099-01-12\",\"horario\":\"10:00\"}"))
                .andExpect(status().isOk());
    }

    private static ConsultaResponse response(StatusConsulta status) {
        return new ConsultaResponse(
                10L, 1L, "Maria Silva", 2L, "Carlos Souza",
                ModalidadeConsulta.ROTINA, null,
                LocalDate.of(2099, 1, 5), LocalTime.of(9, 0),
                "Avaliação de rotina", status == StatusConsulta.CONCLUIDA ? "Paciente avaliado." : null,
                status, status.getDescricao());
    }
}
