package trabalhoPratico.sistemaHospitalar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import trabalhoPratico.sistemaHospitalar.dto.ConclusaoProcedimentoRequest;
import trabalhoPratico.sistemaHospitalar.dto.ProcedimentoCirurgicoRequest;
import trabalhoPratico.sistemaHospitalar.dto.ProcedimentoCirurgicoResponse;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.Cirurgiao;
import trabalhoPratico.sistemaHospitalar.model.Endereco;
import trabalhoPratico.sistemaHospitalar.model.MedicoClinicoGeral;
import trabalhoPratico.sistemaHospitalar.model.Paciente;
import trabalhoPratico.sistemaHospitalar.model.ProcedimentoCirurgico;
import trabalhoPratico.sistemaHospitalar.model.ProfissionalSaude;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusProcedimento;
import trabalhoPratico.sistemaHospitalar.model.enums.TipoCirurgia;
import trabalhoPratico.sistemaHospitalar.repository.PacienteRepository;
import trabalhoPratico.sistemaHospitalar.repository.ProcedimentoCirurgicoRepository;
import trabalhoPratico.sistemaHospitalar.repository.ProfissionalSaudeRepository;

class ProcedimentoCirurgicoServiceTest {

    private ProcedimentoCirurgicoRepository procedimentoRepository;
    private PacienteRepository pacienteRepository;
    private ProfissionalSaudeRepository profissionalRepository;
    private ProcedimentoCirurgicoService service;

    @BeforeEach
    void preparar() {
        procedimentoRepository = mock(ProcedimentoCirurgicoRepository.class);
        pacienteRepository = mock(PacienteRepository.class);
        profissionalRepository = mock(ProfissionalSaudeRepository.class);
        service = new ProcedimentoCirurgicoService(
                procedimentoRepository, pacienteRepository, profissionalRepository);
        when(procedimentoRepository.saveAndFlush(any(ProcedimentoCirurgico.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    @Test
    void agendaProcedimentoCirurgico() {
        prepararAssociacoes(cirurgiaoComExpediente());

        ProcedimentoCirurgicoResponse resposta = service.agendar(requestPadrao());

        assertEquals(StatusProcedimento.AGENDADO, resposta.status());
        assertEquals(TipoCirurgia.ELETIVA, resposta.tipoCirurgia());
        assertEquals("Cirurgia cardíaca", resposta.descricao());
    }

    @Test
    void exigeProfissionalCirurgiao() {
        prepararAssociacoes(new MedicoClinicoGeral(
                "Carlos Souza", "31999990000", "carlos@hospital.com", "CRM-MG 100"));

        assertThrows(RegraDeNegocioException.class, () -> service.agendar(requestPadrao()));
    }

    @Test
    void exigeExpedienteDoCirurgiao() {
        prepararAssociacoes(new Cirurgiao(
                "Paulo Rocha", "31999992222", "paulo@hospital.com", "CRM-MG 300", "Cardíaca"));

        assertThrows(RegraDeNegocioException.class, () -> service.agendar(requestPadrao()));
    }

    @Test
    void naoConcluiProcedimentoSemIniciar() {
        ProcedimentoCirurgico procedimento = procedimentoAgendado();
        when(procedimentoRepository.findById(1L)).thenReturn(Optional.of(procedimento));

        assertThrows(RegraDeNegocioException.class, () -> service.concluir(
                1L, new ConclusaoProcedimentoRequest("Procedimento sem intercorrências.")));
    }

    @Test
    void iniciaEConcluiRegistrandoDataEfetiva() {
        ProcedimentoCirurgico procedimento = procedimentoAgendado();
        when(procedimentoRepository.findById(1L)).thenReturn(Optional.of(procedimento));

        ProcedimentoCirurgicoResponse iniciado = service.iniciar(1L);
        assertEquals(StatusProcedimento.EM_ANDAMENTO, iniciado.status());
        assertNotNull(iniciado.dataEfetiva());
        assertNotNull(iniciado.horarioEfetivo());

        ProcedimentoCirurgicoResponse concluido = service.concluir(
                1L, new ConclusaoProcedimentoRequest("Procedimento sem intercorrências."));
        assertEquals(StatusProcedimento.CONCLUIDO, concluido.status());
        assertEquals("Procedimento sem intercorrências.", concluido.observacoesPosOperatorias());
    }

    @Test
    void cancelaProcedimentoAgendado() {
        ProcedimentoCirurgico procedimento = procedimentoAgendado();
        when(procedimentoRepository.findById(1L)).thenReturn(Optional.of(procedimento));

        assertEquals(StatusProcedimento.CANCELADO, service.cancelar(1L).status());
    }

    @Test
    void naoCancelaProcedimentoEmAndamento() {
        ProcedimentoCirurgico procedimento = procedimentoAgendado();
        procedimento.iniciar();
        when(procedimentoRepository.findById(1L)).thenReturn(Optional.of(procedimento));

        assertThrows(RegraDeNegocioException.class, () -> service.cancelar(1L));
    }

    @Test
    void informaQuandoProcedimentoNaoExiste() {
        when(procedimentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    private void prepararAssociacoes(ProfissionalSaude profissional) {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente()));
        when(profissionalRepository.findById(2L)).thenReturn(Optional.of(profissional));
    }

    private static ProcedimentoCirurgicoRequest requestPadrao() {
        return new ProcedimentoCirurgicoRequest(
                1L, 2L, proximaSegunda(), LocalTime.of(9, 0),
                "Cirurgia cardíaca", TipoCirurgia.ELETIVA, "Jejum de oito horas.");
    }

    private static ProcedimentoCirurgico procedimentoAgendado() {
        return new ProcedimentoCirurgico(
                paciente(), cirurgiaoComExpediente(), proximaSegunda(), LocalTime.of(9, 0),
                "Cirurgia cardíaca", TipoCirurgia.ELETIVA, "Jejum de oito horas.");
    }

    private static Cirurgiao cirurgiaoComExpediente() {
        Cirurgiao cirurgiao = new Cirurgiao(
                "Paulo Rocha", "31999992222", "paulo@hospital.com", "CRM-MG 300", "Cardíaca");
        cirurgiao.adicionarDisponibilidade(
                DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0), 60);
        return cirurgiao;
    }

    private static Paciente paciente() {
        return new Paciente(
                "Maria Silva", "31999999999", "maria@exemplo.com", "12345678901",
                LocalDate.of(1990, 1, 1),
                new Endereco("Rua A", "10", null, "Centro", "Belo Horizonte", "MG", "30110000"));
    }

    private static LocalDate proximaSegunda() {
        return LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    }
}
