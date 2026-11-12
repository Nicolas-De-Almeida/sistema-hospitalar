package trabalhoPratico.sistemaHospitalar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import trabalhoPratico.sistemaHospitalar.dto.ConclusaoEmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.dto.EmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.dto.EmergenciaResponse;
import trabalhoPratico.sistemaHospitalar.dto.InicioEmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.dto.PrioridadeEmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.AtendimentoEmergencia;
import trabalhoPratico.sistemaHospitalar.model.Endereco;
import trabalhoPratico.sistemaHospitalar.model.MedicoClinicoGeral;
import trabalhoPratico.sistemaHospitalar.model.Paciente;
import trabalhoPratico.sistemaHospitalar.model.enums.PrioridadeEmergencia;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusEmergencia;
import trabalhoPratico.sistemaHospitalar.repository.AtendimentoEmergenciaRepository;
import trabalhoPratico.sistemaHospitalar.repository.PacienteRepository;
import trabalhoPratico.sistemaHospitalar.repository.ProfissionalSaudeRepository;

class EmergenciaServiceTest {

    private AtendimentoEmergenciaRepository emergenciaRepository;
    private PacienteRepository pacienteRepository;
    private ProfissionalSaudeRepository profissionalRepository;
    private EmergenciaService service;

    @BeforeEach
    void preparar() {
        emergenciaRepository = mock(AtendimentoEmergenciaRepository.class);
        pacienteRepository = mock(PacienteRepository.class);
        profissionalRepository = mock(ProfissionalSaudeRepository.class);
        service = new EmergenciaService(
                emergenciaRepository, pacienteRepository, profissionalRepository);
        when(emergenciaRepository.saveAndFlush(any(AtendimentoEmergencia.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    @Test
    void registraEmergenciaCriticaAguardandoAvaliacao() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente()));

        EmergenciaResponse resposta = service.registrar(
                new EmergenciaRequest(1L, PrioridadeEmergencia.CRITICA, "Dor intensa no peito"));

        assertEquals(StatusEmergencia.AGUARDANDO_AVALIACAO, resposta.status());
        assertEquals(PrioridadeEmergencia.CRITICA, resposta.prioridade());
        assertNull(resposta.profissionalId());
    }

    @Test
    void filaOrdenaMaiorPrioridadePrimeiro() {
        AtendimentoEmergencia baixa = new AtendimentoEmergencia(
                paciente(), PrioridadeEmergencia.BAIXA, "Dor leve");
        AtendimentoEmergencia critica = new AtendimentoEmergencia(
                paciente(), PrioridadeEmergencia.CRITICA, "Parada cardiorrespiratória");
        when(emergenciaRepository.findByStatus(StatusEmergencia.AGUARDANDO_AVALIACAO))
                .thenReturn(List.of(baixa, critica));

        List<EmergenciaResponse> fila = service.listarFila();

        assertEquals(PrioridadeEmergencia.CRITICA, fila.getFirst().prioridade());
        assertEquals(PrioridadeEmergencia.BAIXA, fila.getLast().prioridade());
    }

    @Test
    void iniciaEConcluiAvaliacao() {
        AtendimentoEmergencia emergencia = emergenciaAguardando();
        MedicoClinicoGeral medico = medico();
        when(emergenciaRepository.findById(1L)).thenReturn(Optional.of(emergencia));
        when(profissionalRepository.findById(2L)).thenReturn(Optional.of(medico));

        assertEquals(StatusEmergencia.EM_ATENDIMENTO,
                service.iniciarAvaliacao(1L, new InicioEmergenciaRequest(2L)).status());
        EmergenciaResponse concluida = service.concluir(
                1L, new ConclusaoEmergenciaRequest("Paciente estabilizado."));

        assertEquals(StatusEmergencia.CONCLUIDO, concluida.status());
        assertEquals("Paciente estabilizado.", concluida.observacoes());
    }

    @Test
    void rejeitaProfissionalInativo() {
        AtendimentoEmergencia emergencia = emergenciaAguardando();
        MedicoClinicoGeral medico = medico();
        medico.setAtivo(false);
        when(emergenciaRepository.findById(1L)).thenReturn(Optional.of(emergencia));
        when(profissionalRepository.findById(2L)).thenReturn(Optional.of(medico));

        assertThrows(RegraDeNegocioException.class,
                () -> service.iniciarAvaliacao(1L, new InicioEmergenciaRequest(2L)));
    }

    @Test
    void naoConcluiAntesDeIniciar() {
        when(emergenciaRepository.findById(1L)).thenReturn(Optional.of(emergenciaAguardando()));

        assertThrows(RegraDeNegocioException.class,
                () -> service.concluir(1L, new ConclusaoEmergenciaRequest("Observações")));
    }

    @Test
    void permiteReclassificacaoAntesDaConclusao() {
        AtendimentoEmergencia emergencia = emergenciaAguardando();
        when(emergenciaRepository.findById(1L)).thenReturn(Optional.of(emergencia));

        assertEquals(PrioridadeEmergencia.CRITICA,
                service.reclassificar(
                        1L, new PrioridadeEmergenciaRequest(PrioridadeEmergencia.CRITICA)).prioridade());
    }

    @Test
    void naoReclassificaAtendimentoConcluido() {
        AtendimentoEmergencia emergencia = emergenciaAguardando();
        emergencia.iniciarAvaliacao(medico());
        emergencia.concluir(null);
        when(emergenciaRepository.findById(1L)).thenReturn(Optional.of(emergencia));

        assertThrows(RegraDeNegocioException.class, () -> service.reclassificar(
                1L, new PrioridadeEmergenciaRequest(PrioridadeEmergencia.ALTA)));
    }

    @Test
    void informaQuandoEmergenciaNaoExiste() {
        when(emergenciaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    private static AtendimentoEmergencia emergenciaAguardando() {
        return new AtendimentoEmergencia(
                paciente(), PrioridadeEmergencia.ALTA, "Falta de ar");
    }

    private static MedicoClinicoGeral medico() {
        return new MedicoClinicoGeral(
                "Carlos Souza", "31999990000", "carlos@hospital.com", "CRM-MG 100");
    }

    private static Paciente paciente() {
        return new Paciente(
                "Maria Silva", "31999999999", "maria@exemplo.com", "12345678901",
                LocalDate.of(1990, 1, 1),
                new Endereco("Rua A", "10", null, "Centro", "Belo Horizonte", "MG", "30110000"));
    }
}
