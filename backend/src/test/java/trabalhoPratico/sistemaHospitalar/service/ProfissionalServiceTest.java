package trabalhoPratico.sistemaHospitalar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import trabalhoPratico.sistemaHospitalar.dto.DisponibilidadeRequest;
import trabalhoPratico.sistemaHospitalar.dto.DisponibilidadeResponse;
import trabalhoPratico.sistemaHospitalar.dto.ProfissionalRequest;
import trabalhoPratico.sistemaHospitalar.dto.ProfissionalResponse;
import trabalhoPratico.sistemaHospitalar.dto.StatusProfissionalRequest;
import trabalhoPratico.sistemaHospitalar.exception.ConflitoException;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.Cirurgiao;
import trabalhoPratico.sistemaHospitalar.model.MedicoClinicoGeral;
import trabalhoPratico.sistemaHospitalar.model.MedicoEspecialista;
import trabalhoPratico.sistemaHospitalar.model.ProfissionalSaude;
import trabalhoPratico.sistemaHospitalar.model.enums.CategoriaProfissional;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;
import trabalhoPratico.sistemaHospitalar.repository.DisponibilidadeAtendimentoRepository;
import trabalhoPratico.sistemaHospitalar.repository.ProfissionalSaudeRepository;

class ProfissionalServiceTest {

    private ProfissionalSaudeRepository repository;
    private DisponibilidadeAtendimentoRepository disponibilidadeRepository;
    private ProfissionalService service;

    @BeforeEach
    void preparar() {
        repository = mock(ProfissionalSaudeRepository.class);
        disponibilidadeRepository = mock(DisponibilidadeAtendimentoRepository.class);
        service = new ProfissionalService(repository, disponibilidadeRepository);
        when(repository.saveAndFlush(any(ProfissionalSaude.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
        when(disponibilidadeRepository.saveAndFlush(any()))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    @Test
    void cadastraClinicoGeral() {
        ProfissionalResponse resposta = service.cadastrar(requestClinico());

        assertEquals(CategoriaProfissional.CLINICO_GERAL, resposta.categoria());
        assertEquals(Especialidade.CLINICO_GERAL, resposta.especialidade());
        assertEquals("CRM-MG 100", resposta.registroProfissional());
    }

    @Test
    void cadastraEspecialistaComSuaEspecialidade() {
        ProfissionalResponse resposta = service.cadastrar(requestEspecialista());

        assertEquals(CategoriaProfissional.ESPECIALISTA, resposta.categoria());
        assertEquals(Especialidade.CARDIOLOGIA, resposta.especialidade());
    }

    @Test
    void cadastraCirurgiaoComAreaCirurgica() {
        ProfissionalResponse resposta = service.cadastrar(requestCirurgiao());

        assertEquals(CategoriaProfissional.CIRURGIAO, resposta.categoria());
        assertEquals("Cirurgia cardíaca", resposta.areaCirurgica());
    }

    @Test
    void rejeitaRegistroProfissionalDuplicado() {
        when(repository.existsByRegistroProfissional("CRM-MG 100")).thenReturn(true);

        assertThrows(ConflitoException.class, () -> service.cadastrar(requestClinico()));
    }

    @Test
    void filtraProfissionaisPorEspecialidade() {
        when(repository.findByEspecialidade(Especialidade.CARDIOLOGIA))
                .thenReturn(List.of(especialista()));

        List<ProfissionalResponse> resposta = service.listar(Especialidade.CARDIOLOGIA);

        assertEquals(1, resposta.size());
        assertEquals(Especialidade.CARDIOLOGIA, resposta.getFirst().especialidade());
        verify(repository).findByEspecialidade(Especialidade.CARDIOLOGIA);
    }

    @Test
    void naoPermiteAlterarCategoriaDoProfissional() {
        when(repository.findById(1L)).thenReturn(Optional.of(clinico()));

        assertThrows(RegraDeNegocioException.class,
                () -> service.atualizar(1L, requestEspecialista()));
    }

    @Test
    void desativaProfissional() {
        MedicoClinicoGeral profissional = clinico();
        when(repository.findById(1L)).thenReturn(Optional.of(profissional));

        ProfissionalResponse resposta = service.alterarStatus(1L, new StatusProfissionalRequest(false));

        assertFalse(resposta.ativo());
    }

    @Test
    void adicionaDisponibilidade() {
        MedicoClinicoGeral profissional = clinico();
        when(repository.findById(1L)).thenReturn(Optional.of(profissional));

        DisponibilidadeResponse resposta = service.adicionarDisponibilidade(
                1L, new DisponibilidadeRequest(
                        DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0), 30));

        assertEquals(DayOfWeek.MONDAY, resposta.diaSemana());
        assertEquals(1, profissional.getDisponibilidades().size());
    }

    @Test
    void rejeitaDisponibilidadesSobrepostas() {
        MedicoClinicoGeral profissional = clinico();
        profissional.adicionarDisponibilidade(
                DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        when(repository.findById(1L)).thenReturn(Optional.of(profissional));

        assertThrows(RegraDeNegocioException.class, () -> service.adicionarDisponibilidade(
                1L, new DisponibilidadeRequest(
                        DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(14, 0), 30)));
    }

    @Test
    void informaQuandoProfissionalNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void profissionalNovoComecaAtivo() {
        assertTrue(service.cadastrar(requestClinico()).ativo());
    }

    private static ProfissionalRequest requestClinico() {
        return new ProfissionalRequest(
                "Carlos Souza", "31999990000", "carlos@hospital.com", " crm-mg 100 ",
                CategoriaProfissional.CLINICO_GERAL, null, null);
    }

    private static ProfissionalRequest requestEspecialista() {
        return new ProfissionalRequest(
                "Ana Lima", "31999991111", "ana@hospital.com", "CRM-MG 200",
                CategoriaProfissional.ESPECIALISTA, Especialidade.CARDIOLOGIA, null);
    }

    private static ProfissionalRequest requestCirurgiao() {
        return new ProfissionalRequest(
                "Paulo Rocha", "31999992222", "paulo@hospital.com", "CRM-MG 300",
                CategoriaProfissional.CIRURGIAO, null, "Cirurgia cardíaca");
    }

    private static MedicoClinicoGeral clinico() {
        return new MedicoClinicoGeral(
                "Carlos Souza", "31999990000", "carlos@hospital.com", "CRM-MG 100");
    }

    private static MedicoEspecialista especialista() {
        return new MedicoEspecialista(
                "Ana Lima", "31999991111", "ana@hospital.com", "CRM-MG 200",
                Especialidade.CARDIOLOGIA);
    }

    @SuppressWarnings("unused")
    private static Cirurgiao cirurgiao() {
        return new Cirurgiao(
                "Paulo Rocha", "31999992222", "paulo@hospital.com", "CRM-MG 300",
                "Cirurgia cardíaca");
    }
}
