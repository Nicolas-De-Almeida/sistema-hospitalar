package trabalhoPratico.sistemaHospitalar.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:api-integration;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "app.seed-admin=false"
})
@AutoConfigureMockMvc
class ApiSprint2IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fluxoCompletoDaApiPersisteDadosEAplicaRegrasDeNegocio() throws Exception {
        LocalDate dataAgendamento = LocalDate.now().plusWeeks(1);
        String diaSemana = dataAgendamento.getDayOfWeek().name();

        String pacienteJson = """
                {
                  "nome": "Paciente Integração",
                  "telefone": "31999990000",
                  "email": "paciente.integracao@hospital.com",
                  "cpf": "98765432100",
                  "dataNascimento": "1990-05-10",
                  "endereco": {
                    "logradouro": "Rua dos Testes",
                    "numero": "100",
                    "bairro": "Centro",
                    "cidade": "Belo Horizonte",
                    "uf": "MG",
                    "cep": "30110000"
                  }
                }
                """;
        long pacienteId = postarEObterId("/api/pacientes", pacienteJson);

        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pacienteJson))
                .andExpect(status().isConflict());

        long clinicoId = postarEObterId("/api/profissionais", """
                {
                  "nome": "Dra. Clínica",
                  "telefone": "31999991111",
                  "email": "clinica@hospital.com",
                  "registroProfissional": "CRM-MG-10001",
                  "categoria": "CLINICO_GERAL"
                }
                """);

        long cirurgiaoId = postarEObterId("/api/profissionais", """
                {
                  "nome": "Dr. Cirurgião",
                  "telefone": "31999992222",
                  "email": "cirurgiao@hospital.com",
                  "registroProfissional": "CRM-MG-20002",
                  "categoria": "CIRURGIAO",
                  "areaCirurgica": "Cirurgia geral"
                }
                """);

        String disponibilidadeJson = """
                {
                  "diaSemana": "%s",
                  "horaInicio": "08:00",
                  "horaFim": "18:00",
                  "duracaoConsultaMin": 30
                }
                """.formatted(diaSemana);
        postarEObterId("/api/profissionais/" + clinicoId + "/disponibilidades", disponibilidadeJson);
        postarEObterId("/api/profissionais/" + cirurgiaoId + "/disponibilidades", disponibilidadeJson);

        long quartoId = postarEObterId("/api/quartos", """
                {
                  "numero": "INT-101",
                  "andar": 1,
                  "capacidadeMaxima": 1,
                  "tipo": "UTI"
                }
                """);

        long consultaId = postarEObterId("/api/consultas", """
                {
                  "pacienteId": %d,
                  "profissionalId": %d,
                  "modalidade": "ROTINA",
                  "data": "%s",
                  "horario": "09:00",
                  "motivo": "Avaliação clínica integrada"
                }
                """.formatted(pacienteId, clinicoId, dataAgendamento));

        mockMvc.perform(patch("/api/consultas/{id}/confirmar", consultaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMADA"));
        mockMvc.perform(patch("/api/consultas/{id}/iniciar", consultaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ATENDIMENTO"));
        mockMvc.perform(patch("/api/consultas/{id}/concluir", consultaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"observacoes\":\"Paciente avaliado sem intercorrências.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));

        long internacaoId = postarEObterId("/api/internacoes", """
                {
                  "pacienteId": %d,
                  "profissionalId": %d,
                  "quartoId": %d,
                  "dataEntrada": "%s",
                  "dataPrevistaAlta": "%s",
                  "observacoes": "Internação para observação"
                }
                """.formatted(
                        pacienteId,
                        clinicoId,
                        quartoId,
                        LocalDate.now(),
                        LocalDate.now().plusDays(2)));

        mockMvc.perform(get("/api/quartos/{id}", quartoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ocupacaoAtual").value(1))
                .andExpect(jsonPath("$.situacao").value("OCUPADO"));

        mockMvc.perform(patch("/api/internacoes/{id}/alta", internacaoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dataAlta\":\"%s\"}".formatted(LocalDate.now())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENCERRADA"));

        mockMvc.perform(get("/api/quartos/{id}", quartoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ocupacaoAtual").value(0))
                .andExpect(jsonPath("$.situacao").value("DISPONIVEL"));

        long emergenciaId = postarEObterId("/api/emergencias", """
                {
                  "pacienteId": %d,
                  "prioridade": "CRITICA",
                  "queixaPrincipal": "Dor intensa"
                }
                """.formatted(pacienteId));

        mockMvc.perform(get("/api/emergencias/fila"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(emergenciaId))
                .andExpect(jsonPath("$[0].prioridade").value("CRITICA"));
        mockMvc.perform(patch("/api/emergencias/{id}/iniciar", emergenciaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profissionalId\":%d}".formatted(clinicoId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ATENDIMENTO"));
        mockMvc.perform(patch("/api/emergencias/{id}/concluir", emergenciaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"observacoes\":\"Paciente estabilizado.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDO"));

        long procedimentoId = postarEObterId("/api/procedimentos", """
                {
                  "pacienteId": %d,
                  "cirurgiaoId": %d,
                  "dataPrevista": "%s",
                  "horarioPrevisto": "11:00",
                  "descricao": "Procedimento cirúrgico integrado",
                  "tipoCirurgia": "ELETIVA",
                  "observacoesPreOperatorias": "Jejum confirmado"
                }
                """.formatted(pacienteId, cirurgiaoId, dataAgendamento));

        mockMvc.perform(patch("/api/procedimentos/{id}/concluir", procedimentoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"observacoesPosOperatorias\":\"Sem intercorrências.\"}"))
                .andExpect(status().isUnprocessableEntity());
        mockMvc.perform(patch("/api/procedimentos/{id}/iniciar", procedimentoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
        mockMvc.perform(patch("/api/procedimentos/{id}/concluir", procedimentoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"observacoesPosOperatorias\":\"Sem intercorrências.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDO"));

        mockMvc.perform(get("/api/pacientes/{id}/historico", pacienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paciente.id").value(pacienteId))
                .andExpect(jsonPath("$.consultas[0].id").value(consultaId))
                .andExpect(jsonPath("$.consultas[0].status").value("CONCLUIDA"))
                .andExpect(jsonPath("$.internacoes[0].id").value(internacaoId))
                .andExpect(jsonPath("$.internacoes[0].status").value("ENCERRADA"));
    }

    private long postarEObterId(String url, String json) throws Exception {
        MvcResult resultado = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();
        Number id = JsonPath.read(resultado.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }
}
