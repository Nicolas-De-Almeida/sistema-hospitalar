package trabalhoPratico.sistemaHospitalar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import trabalhoPratico.sistemaHospitalar.dto.QuartoRequest;
import trabalhoPratico.sistemaHospitalar.dto.QuartoResponse;
import trabalhoPratico.sistemaHospitalar.exception.ConflitoException;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.Endereco;
import trabalhoPratico.sistemaHospitalar.model.Internacao;
import trabalhoPratico.sistemaHospitalar.model.MedicoClinicoGeral;
import trabalhoPratico.sistemaHospitalar.model.Paciente;
import trabalhoPratico.sistemaHospitalar.model.Quarto;
import trabalhoPratico.sistemaHospitalar.model.enums.SituacaoQuarto;
import trabalhoPratico.sistemaHospitalar.model.enums.TipoQuarto;
import trabalhoPratico.sistemaHospitalar.repository.QuartoRepository;

class QuartoServiceTest {

    private QuartoRepository repository;
    private QuartoService service;

    @BeforeEach
    void preparar() {
        repository = mock(QuartoRepository.class);
        service = new QuartoService(repository);
        when(repository.saveAndFlush(any(Quarto.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    @Test
    void cadastraQuartoNormalizandoNumero() {
        QuartoResponse resposta = service.cadastrar(
                new QuartoRequest(" uti-01 ", 2, 1, TipoQuarto.UTI));

        assertEquals("UTI-01", resposta.numero());
        assertEquals(1, resposta.vagasDisponiveis());
        assertEquals(SituacaoQuarto.DISPONIVEL, resposta.situacao());
    }

    @Test
    void rejeitaNumeroDuplicado() {
        when(repository.existsByNumero("101")).thenReturn(true);

        assertThrows(ConflitoException.class, () -> service.cadastrar(
                new QuartoRequest("101", 1, 2, TipoQuarto.APARTAMENTO)));
    }

    @Test
    void rejeitaCapacidadeAcimaDoLimiteDoTipo() {
        assertThrows(RegraDeNegocioException.class, () -> service.cadastrar(
                new QuartoRequest("UTI-02", 2, 2, TipoQuarto.UTI)));
    }

    @Test
    void listaTodosOsQuartos() {
        when(repository.findAll()).thenReturn(List.of(
                new Quarto("101", 1, 2, TipoQuarto.APARTAMENTO),
                new Quarto("201", 2, 1, TipoQuarto.UTI)));

        assertEquals(2, service.listar().size());
    }

    @Test
    void listaSomenteQuartosComVaga() {
        Quarto disponivel = new Quarto("101", 1, 2, TipoQuarto.APARTAMENTO);
        Quarto ocupado = quartoOcupado();
        when(repository.findAll()).thenReturn(List.of(disponivel, ocupado));

        List<QuartoResponse> resposta = service.listarDisponiveis();

        assertEquals(1, resposta.size());
        assertEquals("101", resposta.getFirst().numero());
    }

    @Test
    void buscaQuartoPorId() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(new Quarto("101", 1, 2, TipoQuarto.APARTAMENTO)));

        assertEquals("101", service.buscarPorId(1L).numero());
    }

    @Test
    void informaQuandoQuartoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void atualizaTipoECapacidadeComoUmaOperacao() {
        Quarto quarto = new Quarto("UTI-01", 2, 1, TipoQuarto.UTI);
        when(repository.findById(1L)).thenReturn(Optional.of(quarto));

        QuartoResponse resposta = service.atualizar(
                1L, new QuartoRequest("ENF-01", 2, 6, TipoQuarto.ENFERMARIA));

        assertEquals(TipoQuarto.ENFERMARIA, resposta.tipo());
        assertEquals(6, resposta.capacidadeMaxima());
        verify(repository).saveAndFlush(quarto);
    }

    @Test
    void quartoLotadoApresentaOcupacaoSemVagas() {
        QuartoResponse resposta = QuartoResponse.de(quartoOcupado());

        assertEquals(1, resposta.ocupacaoAtual());
        assertEquals(0, resposta.vagasDisponiveis());
        assertEquals(SituacaoQuarto.OCUPADO, resposta.situacao());
    }

    private static Quarto quartoOcupado() {
        Quarto quarto = new Quarto("UTI-03", 2, 1, TipoQuarto.UTI);
        Paciente paciente = new Paciente(
                "Maria Silva", "31999999999", "maria@exemplo.com", "12345678901",
                LocalDate.of(1990, 1, 1),
                new Endereco("Rua A", "10", null, "Centro", "Belo Horizonte", "MG", "30110000"));
        MedicoClinicoGeral medico = new MedicoClinicoGeral(
                "Carlos Souza", "31999990000", "carlos@hospital.com", "CRM-MG 100");
        new Internacao(
                paciente, medico, quarto, LocalDate.now(), LocalDate.now().plusDays(3), null);
        return quarto;
    }
}
