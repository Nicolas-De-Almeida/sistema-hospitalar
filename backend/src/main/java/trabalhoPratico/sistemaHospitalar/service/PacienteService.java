package trabalhoPratico.sistemaHospitalar.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabalhoPratico.sistemaHospitalar.dto.EnderecoRequest;
import trabalhoPratico.sistemaHospitalar.dto.HistoricoMedicoResponse;
import trabalhoPratico.sistemaHospitalar.dto.PacienteRequest;
import trabalhoPratico.sistemaHospitalar.dto.PacienteResponse;
import trabalhoPratico.sistemaHospitalar.exception.ConflitoException;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.model.Endereco;
import trabalhoPratico.sistemaHospitalar.model.Paciente;
import trabalhoPratico.sistemaHospitalar.repository.PacienteRepository;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional
    public PacienteResponse cadastrar(PacienteRequest request) {
        String cpf = normalizarCpf(request.cpf());
        validarCpfDisponivel(cpf, null);

        Paciente paciente = new Paciente(
                request.nome(),
                request.telefone(),
                request.email(),
                cpf,
                request.dataNascimento(),
                criarEndereco(request.endereco()));

        return PacienteResponse.de(pacienteRepository.saveAndFlush(paciente));
    }

    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {
        return pacienteRepository.findAll().stream()
                .map(PacienteResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public PacienteResponse buscarPorId(Long id) {
        return PacienteResponse.de(buscarEntidade(id));
    }

    @Transactional
    public PacienteResponse atualizar(Long id, PacienteRequest request) {
        Paciente paciente = buscarEntidade(id);
        String cpf = normalizarCpf(request.cpf());
        validarCpfDisponivel(cpf, id);

        paciente.setNome(request.nome());
        paciente.setTelefone(request.telefone());
        paciente.setEmail(request.email());
        paciente.setCpf(cpf);
        paciente.setDataNascimento(request.dataNascimento());
        paciente.setEndereco(criarEndereco(request.endereco()));

        return PacienteResponse.de(pacienteRepository.saveAndFlush(paciente));
    }

    @Transactional(readOnly = true)
    public HistoricoMedicoResponse consultarHistorico(Long id) {
        return HistoricoMedicoResponse.de(buscarEntidade(id));
    }

    private Paciente buscarEntidade(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado: " + id + "."));
    }

    private void validarCpfDisponivel(String cpf, Long idAtual) {
        boolean duplicado = idAtual == null
                ? pacienteRepository.existsByCpf(cpf)
                : pacienteRepository.existsByCpfAndIdNot(cpf, idAtual);
        if (duplicado) {
            throw new ConflitoException("Já existe um paciente cadastrado com este CPF.");
        }
    }

    private static String normalizarCpf(String cpf) {
        return cpf.replaceAll("\\D", "");
    }

    private static Endereco criarEndereco(EnderecoRequest request) {
        return new Endereco(
                request.logradouro(),
                request.numero(),
                request.complemento(),
                request.bairro(),
                request.cidade(),
                request.uf(),
                request.cep());
    }
}
