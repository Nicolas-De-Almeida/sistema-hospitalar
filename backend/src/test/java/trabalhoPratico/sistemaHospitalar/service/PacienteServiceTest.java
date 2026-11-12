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

import trabalhoPratico.sistemaHospitalar.dto.EnderecoRequest;
import trabalhoPratico.sistemaHospitalar.dto.HistoricoMedicoResponse;
import trabalhoPratico.sistemaHospitalar.dto.PacienteRequest;
import trabalhoPratico.sistemaHospitalar.dto.PacienteResponse;
import trabalhoPratico.sistemaHospitalar.exception.ConflitoException;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.model.Endereco;
import trabalhoPratico.sistemaHospitalar.model.Paciente;
import trabalhoPratico.sistemaHospitalar.repository.PacienteRepository;

class PacienteServiceTest {

    private PacienteRepository repository;
    private PacienteService service;

    @BeforeEach
    void preparar() {
        repository = mock(PacienteRepository.class);
        service = new PacienteService(repository);
    }

    @Test
    void cadastraPacienteNormalizandoCpfEEmail() {
        when(repository.existsByCpf("12345678901")).thenReturn(false);
        when(repository.saveAndFlush(any(Paciente.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        PacienteResponse resposta = service.cadastrar(requestPadrao());

        assertEquals("12345678901", resposta.cpf());
        assertEquals("maria@exemplo.com", resposta.email());
        assertEquals("MG", resposta.endereco().uf());
        verify(repository).saveAndFlush(any(Paciente.class));
    }

    @Test
    void rejeitaCadastroComCpfDuplicado() {
        when(repository.existsByCpf("12345678901")).thenReturn(true);

        assertThrows(ConflitoException.class, () -> service.cadastrar(requestPadrao()));
    }

    @Test
    void listaPacientesSemRetornarEntidades() {
        when(repository.findAll()).thenReturn(List.of(pacientePadrao(), pacientePadrao()));

        List<PacienteResponse> resposta = service.listar();

        assertEquals(2, resposta.size());
    }

    @Test
    void buscaPacientePorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(pacientePadrao()));

        PacienteResponse resposta = service.buscarPorId(1L);

        assertEquals("Maria da Silva", resposta.nome());
    }

    @Test
    void informaQuandoPacienteNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void atualizaPacienteExistente() {
        Paciente paciente = pacientePadrao();
        when(repository.findById(1L)).thenReturn(Optional.of(paciente));
        when(repository.existsByCpfAndIdNot("12345678901", 1L)).thenReturn(false);
        when(repository.saveAndFlush(paciente)).thenReturn(paciente);

        PacienteRequest request = new PacienteRequest(
                "Maria Atualizada", "31999999999", "novo@exemplo.com", "123.456.789-01",
                LocalDate.of(1990, 5, 10), enderecoPadrao());
        PacienteResponse resposta = service.atualizar(1L, request);

        assertEquals("Maria Atualizada", resposta.nome());
        assertEquals("novo@exemplo.com", resposta.email());
    }

    @Test
    void retornaHistoricoVazioQuandoPacienteNaoTemOcorrenciasConcluidas() {
        when(repository.findById(1L)).thenReturn(Optional.of(pacientePadrao()));

        HistoricoMedicoResponse historico = service.consultarHistorico(1L);

        assertEquals(0, historico.consultas().size());
        assertEquals(0, historico.internacoes().size());
        assertEquals(0, historico.registros().size());
    }

    private static PacienteRequest requestPadrao() {
        return new PacienteRequest(
                "Maria da Silva", "31999999999", "MARIA@EXEMPLO.COM", "123.456.789-01",
                LocalDate.of(1990, 5, 10), enderecoPadrao());
    }

    private static EnderecoRequest enderecoPadrao() {
        return new EnderecoRequest(
                "Rua das Flores", "10", "Apto 2", "Centro", "Belo Horizonte", "mg", "30110-000");
    }

    private static Paciente pacientePadrao() {
        return new Paciente(
                "Maria da Silva", "31999999999", "maria@exemplo.com", "12345678901",
                LocalDate.of(1990, 5, 10),
                new Endereco("Rua das Flores", "10", "Apto 2", "Centro", "Belo Horizonte", "MG", "30110000"));
    }
}
