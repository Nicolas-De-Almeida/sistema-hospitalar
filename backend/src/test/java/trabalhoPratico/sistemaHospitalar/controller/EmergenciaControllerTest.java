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

import trabalhoPratico.sistemaHospitalar.dto.ConclusaoEmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.dto.EmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.dto.EmergenciaResponse;
import trabalhoPratico.sistemaHospitalar.dto.InicioEmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.dto.PrioridadeEmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.model.enums.PrioridadeEmergencia;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusEmergencia;
import trabalhoPratico.sistemaHospitalar.service.EmergenciaService;

@WebMvcTest(EmergenciaController.class)
class EmergenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmergenciaService emergenciaService;

    @Test
    void registraEmergenciaRetornando201() throws Exception {
        when(emergenciaService.registrar(any(EmergenciaRequest.class)))
                .thenReturn(response(StatusEmergencia.AGUARDANDO_AVALIACAO));

        mockMvc.perform(post("/api/emergencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pacienteId": 1,
                                  "prioridade": "CRITICA",
                                  "queixaPrincipal": "Dor intensa no peito"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/emergencias/10"))
                .andExpect(jsonPath("$.prioridade").value("CRITICA"));
    }

    @Test
    void dadosInvalidosRetornam400() throws Exception {
        mockMvc.perform(post("/api/emergencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listaFilaPriorizada() throws Exception {
        when(emergenciaService.listarFila())
                .thenReturn(List.of(response(StatusEmergencia.AGUARDANDO_AVALIACAO)));

        mockMvc.perform(get("/api/emergencias/fila"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].prioridade").value("CRITICA"));
    }

    @Test
    void iniciaAvaliacao() throws Exception {
        when(emergenciaService.iniciarAvaliacao(any(Long.class), any(InicioEmergenciaRequest.class)))
                .thenReturn(response(StatusEmergencia.EM_ATENDIMENTO));

        mockMvc.perform(patch("/api/emergencias/10/iniciar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profissionalId\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ATENDIMENTO"));
    }

    @Test
    void reclassificaPrioridade() throws Exception {
        when(emergenciaService.reclassificar(any(Long.class), any(PrioridadeEmergenciaRequest.class)))
                .thenReturn(response(StatusEmergencia.AGUARDANDO_AVALIACAO));

        mockMvc.perform(patch("/api/emergencias/10/prioridade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prioridade\":\"CRITICA\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void concluiEmergencia() throws Exception {
        when(emergenciaService.concluir(any(Long.class), any(ConclusaoEmergenciaRequest.class)))
                .thenReturn(response(StatusEmergencia.CONCLUIDO));

        mockMvc.perform(patch("/api/emergencias/10/concluir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"observacoes\":\"Paciente estabilizado.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDO"));
    }

    private static EmergenciaResponse response(StatusEmergencia status) {
        boolean iniciado = status != StatusEmergencia.AGUARDANDO_AVALIACAO;
        return new EmergenciaResponse(
                10L, 1L, "Maria Silva", iniciado ? 2L : null, iniciado ? "Carlos Souza" : null,
                LocalDate.of(2026, 11, 11), LocalTime.of(10, 0),
                PrioridadeEmergencia.CRITICA, "Dor intensa no peito",
                status == StatusEmergencia.CONCLUIDO ? "Paciente estabilizado." : null,
                status, status.getDescricao());
    }
}
