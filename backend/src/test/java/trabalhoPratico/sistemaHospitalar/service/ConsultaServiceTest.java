package trabalhoPratico.sistemaHospitalar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import trabalhoPratico.sistemaHospitalar.dto.ConclusaoConsultaRequest;
import trabalhoPratico.sistemaHospitalar.dto.ConsultaRequest;
import trabalhoPratico.sistemaHospitalar.dto.ConsultaResponse;
import trabalhoPratico.sistemaHospitalar.dto.RemarcacaoConsultaRequest;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.Consulta;
import trabalhoPratico.sistemaHospitalar.model.ConsultaRotina;
import trabalhoPratico.sistemaHospitalar.model.Endereco;
import trabalhoPratico.sistemaHospitalar.model.MedicoClinicoGeral;
import trabalhoPratico.sistemaHospitalar.model.MedicoEspecialista;
import trabalhoPratico.sistemaHospitalar.model.Paciente;
import trabalhoPratico.sistemaHospitalar.model.ProfissionalSaude;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;
import trabalhoPratico.sistemaHospitalar.model.enums.ModalidadeConsulta;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusConsulta;
import trabalhoPratico.sistemaHospitalar.repository.ConsultaRepository;
import trabalhoPratico.sistemaHospitalar.repository.PacienteRepository;
import trabalhoPratico.sistemaHospitalar.repository.ProfissionalSaudeRepository;

class ConsultaServiceTest {

    private ConsultaRepository consultaRepository;
    private PacienteRepository pacienteRepository;
    private ProfissionalSaudeRepository profissionalRepository;
    private ConsultaService service;

    @BeforeEach
    void preparar() {
        consultaRepository = mock(ConsultaRepository.class);
        pacienteRepository = mock(PacienteRepository.class);
        profissionalRepository = mock(ProfissionalSaudeRepository.class);
        service = new ConsultaService(consultaRepository, pacienteRepository, profissionalRepository);
        when(consultaRepository.saveAndFlush(any(Consulta.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    @Test
    void agendaConsultaDeRotina() {
        LocalDate data = proximaSegunda();
        MedicoClinicoGeral medico = clinicoComExpediente();
        prepararAssociacoes(paciente(), medico);

        ConsultaResponse resposta = service.agendar(requestRotina(data, LocalTime.of(9, 0)));

        assertEquals(ModalidadeConsulta.ROTINA, resposta.modalidade());
        assertEquals(StatusConsulta.AGENDADA, resposta.status());
        assertEquals("Carlos Souza", resposta.profissionalNome());
    }

    @Test
    void agendaConsultaEspecializadaCompativel() {
        LocalDate data = proximaSegunda();
        MedicoEspecialista medico = especialistaComExpediente(Especialidade.CARDIOLOGIA);
        prepararAssociacoes(paciente(), medico);

        ConsultaResponse resposta = service.agendar(requestEspecializada(
                data, LocalTime.of(10, 0), Especialidade.CARDIOLOGIA));

        assertEquals(ModalidadeConsulta.ESPECIALIZADA, resposta.modalidade());
        assertEquals(Especialidade.CARDIOLOGIA, resposta.especialidade());
    }

    @Test
    void rejeitaConsultaSemExpediente() {
        prepararAssociacoes(paciente(), new MedicoClinicoGeral(
                "Carlos Souza", "31999990000", "carlos@hospital.com", "CRM-MG 100"));

        assertThrows(RegraDeNegocioException.class,
                () -> service.agendar(requestRotina(proximaSegunda(), LocalTime.of(9, 0))));
    }

    @Test
    void rejeitaConsultaEspecializadaDeOutraArea() {
        MedicoEspecialista medico = especialistaComExpediente(Especialidade.NEUROLOGIA);
        prepararAssociacoes(paciente(), medico);

        assertThrows(RegraDeNegocioException.class, () -> service.agendar(requestEspecializada(
                proximaSegunda(), LocalTime.of(10, 0), Especialidade.CARDIOLOGIA)));
    }

    @Test
    void rejeitaModalidadeIncompativelComCategoriaDoProfissional() {
        prepararAssociacoes(paciente(), especialistaComExpediente(Especialidade.CARDIOLOGIA));

        assertThrows(RegraDeNegocioException.class,
                () -> service.agendar(requestRotina(proximaSegunda(), LocalTime.of(9, 0))));
    }

    @Test
    void rejeitaConflitoNoMesmoHorario() {
        LocalDate data = proximaSegunda();
        MedicoClinicoGeral medico = clinicoComExpediente();
        new ConsultaRotina(paciente(), medico, data, LocalTime.of(9, 0), "Primeira consulta");
        prepararAssociacoes(paciente(), medico);

        assertThrows(RegraDeNegocioException.class,
                () -> service.agendar(requestRotina(data, LocalTime.of(9, 0))));
    }

    @Test
    void expedientePrecisaCobrirTodaDuracaoDaConsulta() {
        MedicoClinicoGeral medico = clinicoComExpediente();
        prepararAssociacoes(paciente(), medico);

        assertThrows(RegraDeNegocioException.class,
                () -> service.agendar(requestRotina(proximaSegunda(), LocalTime.of(11, 45))));
    }

    @Test
    void executaFluxoCompletoAteConclusao() {
        Consulta consulta = consultaAgendada();
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        assertEquals(StatusConsulta.CONFIRMADA, service.confirmar(1L).status());
        assertEquals(StatusConsulta.EM_ATENDIMENTO, service.iniciar(1L).status());
        ConsultaResponse concluida = service.concluir(
                1L, new ConclusaoConsultaRequest("Paciente avaliado e liberado."));

        assertEquals(StatusConsulta.CONCLUIDA, concluida.status());
        assertEquals("Paciente avaliado e liberado.", concluida.observacoes());
    }

    @Test
    void naoPermiteConcluirConsultaCancelada() {
        Consulta consulta = consultaAgendada();
        consulta.cancelar();
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        assertThrows(RegraDeNegocioException.class,
                () -> service.concluir(1L, new ConclusaoConsultaRequest("Observações")));
    }

    @Test
    void registraNaoComparecimento() {
        Consulta consulta = consultaAgendada();
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        assertEquals(StatusConsulta.NAO_COMPARECEU, service.registrarFalta(1L).status());
    }

    @Test
    void remarcacaoVoltaConsultaConfirmadaParaAgendada() {
        Consulta consulta = consultaAgendada();
        consulta.confirmar();
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        LocalDate novaData = proximaSegunda().plusWeeks(1);

        ConsultaResponse resposta = service.remarcar(
                1L, new RemarcacaoConsultaRequest(novaData, LocalTime.of(10, 0)));

        assertEquals(StatusConsulta.AGENDADA, resposta.status());
        assertEquals(novaData, resposta.data());
    }

    @Test
    void filtraConsultasPorStatus() {
        when(consultaRepository.findByStatus(StatusConsulta.AGENDADA))
                .thenReturn(List.of(consultaAgendada()));

        assertEquals(1, service.listar(StatusConsulta.AGENDADA).size());
        verify(consultaRepository).findByStatus(StatusConsulta.AGENDADA);
    }

    @Test
    void informaQuandoConsultaNaoExiste() {
        when(consultaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    private void prepararAssociacoes(Paciente paciente, ProfissionalSaude profissional) {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(profissionalRepository.findById(2L)).thenReturn(Optional.of(profissional));
    }

    private static ConsultaRequest requestRotina(LocalDate data, LocalTime horario) {
        return new ConsultaRequest(
                1L, 2L, ModalidadeConsulta.ROTINA, data, horario, "Avaliação de rotina", null);
    }

    private static ConsultaRequest requestEspecializada(
            LocalDate data, LocalTime horario, Especialidade especialidade) {
        return new ConsultaRequest(
                1L, 2L, ModalidadeConsulta.ESPECIALIZADA, data, horario,
                "Avaliação especializada", especialidade);
    }

    private static Consulta consultaAgendada() {
        return new ConsultaRotina(
                paciente(), clinicoComExpediente(), proximaSegunda(),
                LocalTime.of(9, 0), "Avaliação de rotina");
    }

    private static MedicoClinicoGeral clinicoComExpediente() {
        MedicoClinicoGeral medico = new MedicoClinicoGeral(
                "Carlos Souza", "31999990000", "carlos@hospital.com", "CRM-MG 100");
        medico.adicionarDisponibilidade(
                DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        return medico;
    }

    private static MedicoEspecialista especialistaComExpediente(Especialidade especialidade) {
        MedicoEspecialista medico = new MedicoEspecialista(
                "Ana Lima", "31999991111", "ana@hospital.com", "CRM-MG 200", especialidade);
        medico.adicionarDisponibilidade(
                DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        return medico;
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
