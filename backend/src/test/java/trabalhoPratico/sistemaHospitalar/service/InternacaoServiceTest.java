package trabalhoPratico.sistemaHospitalar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import trabalhoPratico.sistemaHospitalar.dto.AltaInternacaoRequest;
import trabalhoPratico.sistemaHospitalar.dto.AtualizacaoInternacaoRequest;
import trabalhoPratico.sistemaHospitalar.dto.InternacaoRequest;
import trabalhoPratico.sistemaHospitalar.dto.InternacaoResponse;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.Endereco;
import trabalhoPratico.sistemaHospitalar.model.Internacao;
import trabalhoPratico.sistemaHospitalar.model.MedicoClinicoGeral;
import trabalhoPratico.sistemaHospitalar.model.Paciente;
import trabalhoPratico.sistemaHospitalar.model.Quarto;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusInternacao;
import trabalhoPratico.sistemaHospitalar.model.enums.TipoQuarto;
import trabalhoPratico.sistemaHospitalar.repository.InternacaoRepository;
import trabalhoPratico.sistemaHospitalar.repository.PacienteRepository;
import trabalhoPratico.sistemaHospitalar.repository.ProfissionalSaudeRepository;
import trabalhoPratico.sistemaHospitalar.repository.QuartoRepository;

class InternacaoServiceTest {

    private InternacaoRepository internacaoRepository;
    private PacienteRepository pacienteRepository;
    private ProfissionalSaudeRepository profissionalRepository;
    private QuartoRepository quartoRepository;
    private InternacaoService service;

    @BeforeEach
    void preparar() {
        internacaoRepository = mock(InternacaoRepository.class);
        pacienteRepository = mock(PacienteRepository.class);
        profissionalRepository = mock(ProfissionalSaudeRepository.class);
        quartoRepository = mock(QuartoRepository.class);
        service = new InternacaoService(
                internacaoRepository, pacienteRepository, profissionalRepository, quartoRepository);
        when(internacaoRepository.saveAndFlush(any(Internacao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    @Test
    void criaInternacaoAssociandoPacienteProfissionalEQuarto() {
        Paciente paciente = paciente("12345678901", "Maria Silva");
        MedicoClinicoGeral profissional = profissional();
        Quarto quarto = new Quarto("101", 1, 2, TipoQuarto.APARTAMENTO);
        prepararAssociacoes(paciente, profissional, quarto);

        InternacaoResponse resposta = service.criar(requestPadrao());

        assertEquals("Maria Silva", resposta.pacienteNome());
        assertEquals("Carlos Souza", resposta.profissionalNome());
        assertEquals("101", resposta.quartoNumero());
        assertEquals(StatusInternacao.ATIVA, resposta.status());
        assertEquals(1, quarto.ocupacaoAtual());
    }

    @Test
    void rejeitaPacienteQueJaPossuiInternacaoAtiva() {
        Paciente paciente = paciente("12345678901", "Maria Silva");
        MedicoClinicoGeral profissional = profissional();
        Quarto primeiroQuarto = new Quarto("101", 1, 2, TipoQuarto.APARTAMENTO);
        new Internacao(
                paciente, profissional, primeiroQuarto,
                LocalDate.now(), LocalDate.now().plusDays(2), null);
        Quarto outroQuarto = new Quarto("102", 1, 2, TipoQuarto.APARTAMENTO);
        prepararAssociacoes(paciente, profissional, outroQuarto);

        assertThrows(RegraDeNegocioException.class, () -> service.criar(requestPadrao()));
    }

    @Test
    void rejeitaQuartoLotado() {
        MedicoClinicoGeral profissional = profissional();
        Quarto quarto = new Quarto("UTI-01", 2, 1, TipoQuarto.UTI);
        new Internacao(
                paciente("11111111111", "Primeiro Paciente"), profissional, quarto,
                LocalDate.now(), LocalDate.now().plusDays(2), null);
        Paciente segundoPaciente = paciente("22222222222", "Segundo Paciente");
        prepararAssociacoes(segundoPaciente, profissional, quarto);

        assertThrows(RegraDeNegocioException.class, () -> service.criar(requestPadrao()));
    }

    @Test
    void rejeitaProfissionalInativo() {
        MedicoClinicoGeral profissional = profissional();
        profissional.setAtivo(false);
        prepararAssociacoes(
                paciente("12345678901", "Maria Silva"),
                profissional,
                new Quarto("101", 1, 2, TipoQuarto.APARTAMENTO));

        assertThrows(RegraDeNegocioException.class, () -> service.criar(requestPadrao()));
    }

    @Test
    void informaQuandoPacienteNaoExiste() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.criar(requestPadrao()));
    }

    @Test
    void filtraInternacoesPorStatus() {
        Internacao internacao = internacaoAtiva();
        when(internacaoRepository.findByStatus(StatusInternacao.ATIVA))
                .thenReturn(List.of(internacao));

        List<InternacaoResponse> resposta = service.listar(StatusInternacao.ATIVA);

        assertEquals(1, resposta.size());
        verify(internacaoRepository).findByStatus(StatusInternacao.ATIVA);
    }

    @Test
    void atualizaPrevisaoDeAltaEObservacoes() {
        Internacao internacao = internacaoAtiva();
        when(internacaoRepository.findById(1L)).thenReturn(Optional.of(internacao));
        LocalDate novaPrevisao = LocalDate.now().plusDays(10);

        InternacaoResponse resposta = service.atualizar(
                1L, new AtualizacaoInternacaoRequest(novaPrevisao, "Paciente em observação."));

        assertEquals(novaPrevisao, resposta.dataPrevistaAlta());
        assertEquals("Paciente em observação.", resposta.observacoes());
    }

    @Test
    void darAltaEncerraInternacaoELiberaVaga() {
        Internacao internacao = internacaoAtivaEmUti();
        Quarto quarto = internacao.getQuarto();
        when(internacaoRepository.findById(1L)).thenReturn(Optional.of(internacao));
        assertFalse(quarto.temVaga());

        InternacaoResponse resposta = service.darAlta(
                1L, new AltaInternacaoRequest(LocalDate.now().plusDays(1)));

        assertEquals(StatusInternacao.ENCERRADA, resposta.status());
        assertTrue(quarto.temVaga());
        assertEquals(0, quarto.ocupacaoAtual());
    }

    @Test
    void rejeitaSegundaAlta() {
        Internacao internacao = internacaoAtiva();
        internacao.darAlta(LocalDate.now().plusDays(1));
        when(internacaoRepository.findById(1L)).thenReturn(Optional.of(internacao));

        assertThrows(RegraDeNegocioException.class,
                () -> service.darAlta(1L, new AltaInternacaoRequest(LocalDate.now().plusDays(2))));
    }

    @Test
    void rejeitaAtualizacaoDeInternacaoEncerrada() {
        Internacao internacao = internacaoAtiva();
        internacao.darAlta(LocalDate.now().plusDays(1));
        when(internacaoRepository.findById(1L)).thenReturn(Optional.of(internacao));

        assertThrows(RegraDeNegocioException.class, () -> service.atualizar(
                1L, new AtualizacaoInternacaoRequest(LocalDate.now().plusDays(5), "Alteração")));
    }

    private void prepararAssociacoes(
            Paciente paciente, MedicoClinicoGeral profissional, Quarto quarto) {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(profissionalRepository.findById(2L)).thenReturn(Optional.of(profissional));
        when(quartoRepository.findById(3L)).thenReturn(Optional.of(quarto));
    }

    private static InternacaoRequest requestPadrao() {
        return new InternacaoRequest(
                1L, 2L, 3L, LocalDate.now(), LocalDate.now().plusDays(5), "Observação inicial");
    }

    private static Internacao internacaoAtiva() {
        return new Internacao(
                paciente("12345678901", "Maria Silva"),
                profissional(),
                new Quarto("101", 1, 2, TipoQuarto.APARTAMENTO),
                LocalDate.now(), LocalDate.now().plusDays(5), "Observação inicial");
    }

    private static Internacao internacaoAtivaEmUti() {
        return new Internacao(
                paciente("12345678901", "Maria Silva"),
                profissional(),
                new Quarto("UTI-01", 2, 1, TipoQuarto.UTI),
                LocalDate.now(), LocalDate.now().plusDays(5), null);
    }

    private static Paciente paciente(String cpf, String nome) {
        return new Paciente(
                nome, "31999999999", nome.replace(" ", ".").toLowerCase() + "@exemplo.com", cpf,
                LocalDate.of(1990, 1, 1),
                new Endereco("Rua A", "10", null, "Centro", "Belo Horizonte", "MG", "30110000"));
    }

    private static MedicoClinicoGeral profissional() {
        return new MedicoClinicoGeral(
                "Carlos Souza", "31999990000", "carlos@hospital.com", "CRM-MG 100");
    }
}
