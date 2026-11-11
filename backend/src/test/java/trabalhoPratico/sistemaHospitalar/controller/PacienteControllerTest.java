package trabalhoPratico.sistemaHospitalar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import trabalhoPratico.sistemaHospitalar.dto.EnderecoResponse;
import trabalhoPratico.sistemaHospitalar.dto.PacienteRequest;
import trabalhoPratico.sistemaHospitalar.dto.PacienteResponse;
import trabalhoPratico.sistemaHospitalar.exception.ConflitoException;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.service.PacienteService;

@WebMvcTest(PacienteController.class)
class PacienteControllerTest {

    private static final String JSON_VALIDO = """
            {
              "nome": "Maria da Silva",
              "telefone": "31999999999",
              "email": "maria@exemplo.com",
              "cpf": "123.456.789-01",
              "dataNascimento": "1990-05-10",
              "endereco": {
                "logradouro": "Rua das Flores",
                "numero": "10",
                "complemento": "Apto 2",
                "bairro": "Centro",
                "cidade": "Belo Horizonte",
                "uf": "MG",
                "cep": "30110-000"
              }
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PacienteService pacienteService;

    @Test
    void cadastroValidoRetorna201ELocalizacao() throws Exception {
        when(pacienteService.cadastrar(any(PacienteRequest.class))).thenReturn(responsePadrao());

        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/pacientes/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cpf").value("12345678901"));
    }

    @Test
    void dadosInvalidosRetornam400() throws Exception {
        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").isNotEmpty());
    }

    @Test
    void pacienteInexistenteRetorna404() throws Exception {
        when(pacienteService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Paciente não encontrado: 99."));

        mockMvc.perform(get("/api/pacientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Paciente não encontrado: 99."));
    }

    @Test
    void cpfDuplicadoRetorna409() throws Exception {
        when(pacienteService.cadastrar(any(PacienteRequest.class)))
                .thenThrow(new ConflitoException("Já existe um paciente cadastrado com este CPF."));

        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("Já existe um paciente cadastrado com este CPF."));
    }

    private static PacienteResponse responsePadrao() {
        return new PacienteResponse(
                1L,
                "Maria da Silva",
                "31999999999",
                "maria@exemplo.com",
                "12345678901",
                LocalDate.of(1990, 5, 10),
                36,
                new EnderecoResponse(
                        "Rua das Flores", "10", "Apto 2", "Centro",
                        "Belo Horizonte", "MG", "30110000"));
    }
}
