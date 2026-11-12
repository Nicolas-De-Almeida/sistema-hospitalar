package trabalhoPratico.sistemaHospitalar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import trabalhoPratico.sistemaHospitalar.dto.DisponibilidadeRequest;
import trabalhoPratico.sistemaHospitalar.dto.DisponibilidadeResponse;
import trabalhoPratico.sistemaHospitalar.dto.ProfissionalRequest;
import trabalhoPratico.sistemaHospitalar.dto.ProfissionalResponse;
import trabalhoPratico.sistemaHospitalar.dto.StatusProfissionalRequest;
import trabalhoPratico.sistemaHospitalar.exception.ConflitoException;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.model.enums.CategoriaProfissional;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;
import trabalhoPratico.sistemaHospitalar.service.ProfissionalService;

@WebMvcTest(ProfissionalController.class)
class ProfissionalControllerTest {

    private static final String JSON_CLINICO = """
            {
              "nome": "Carlos Souza",
              "telefone": "31999990000",
              "email": "carlos@hospital.com",
              "registroProfissional": "CRM-MG 100",
              "categoria": "CLINICO_GERAL"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfissionalService profissionalService;

    @Test
    void cadastroValidoRetorna201() throws Exception {
        when(profissionalService.cadastrar(any(ProfissionalRequest.class))).thenReturn(responsePadrao());

        mockMvc.perform(post("/api/profissionais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CLINICO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/profissionais/1"))
                .andExpect(jsonPath("$.categoria").value("CLINICO_GERAL"));
    }

    @Test
    void requisicaoInvalidaRetorna400() throws Exception {
        mockMvc.perform(post("/api/profissionais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registroDuplicadoRetorna409() throws Exception {
        when(profissionalService.cadastrar(any(ProfissionalRequest.class)))
                .thenThrow(new ConflitoException("Registro duplicado."));

        mockMvc.perform(post("/api/profissionais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CLINICO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("Registro duplicado."));
    }

    @Test
    void profissionalInexistenteRetorna404() throws Exception {
        when(profissionalService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Profissional não encontrado: 99."));

        mockMvc.perform(get("/api/profissionais/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void alteraStatusDoProfissional() throws Exception {
        ProfissionalResponse inativo = new ProfissionalResponse(
                1L, "Carlos Souza", "31999990000", "carlos@hospital.com", "CRM-MG 100",
                CategoriaProfissional.CLINICO_GERAL, Especialidade.CLINICO_GERAL,
                null, false, List.of());
        when(profissionalService.alterarStatus(any(Long.class), any(StatusProfissionalRequest.class)))
                .thenReturn(inativo);

        mockMvc.perform(patch("/api/profissionais/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ativo\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    void adicionaDisponibilidadeRetornando201() throws Exception {
        DisponibilidadeResponse response = new DisponibilidadeResponse(
                10L, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        when(profissionalService.adicionarDisponibilidade(
                any(Long.class), any(DisponibilidadeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/profissionais/1/disponibilidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "diaSemana": "MONDAY",
                                  "horaInicio": "08:00",
                                  "horaFim": "12:00",
                                  "duracaoConsultaMin": 30
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location", "http://localhost/api/profissionais/1/disponibilidades/10"))
                .andExpect(jsonPath("$.diaSemana").value("MONDAY"));
    }

    @Test
    void removeDisponibilidadeRetornando204() throws Exception {
        mockMvc.perform(delete("/api/profissionais/1/disponibilidades/10"))
                .andExpect(status().isNoContent());
    }

    private static ProfissionalResponse responsePadrao() {
        return new ProfissionalResponse(
                1L, "Carlos Souza", "31999990000", "carlos@hospital.com", "CRM-MG 100",
                CategoriaProfissional.CLINICO_GERAL, Especialidade.CLINICO_GERAL,
                null, true, List.of());
    }
}
