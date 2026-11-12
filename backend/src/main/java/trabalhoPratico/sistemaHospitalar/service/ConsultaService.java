package trabalhoPratico.sistemaHospitalar.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabalhoPratico.sistemaHospitalar.dto.ConclusaoConsultaRequest;
import trabalhoPratico.sistemaHospitalar.dto.ConsultaRequest;
import trabalhoPratico.sistemaHospitalar.dto.ConsultaResponse;
import trabalhoPratico.sistemaHospitalar.dto.RemarcacaoConsultaRequest;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.Consulta;
import trabalhoPratico.sistemaHospitalar.model.ConsultaEspecializada;
import trabalhoPratico.sistemaHospitalar.model.ConsultaRotina;
import trabalhoPratico.sistemaHospitalar.model.MedicoClinicoGeral;
import trabalhoPratico.sistemaHospitalar.model.MedicoEspecialista;
import trabalhoPratico.sistemaHospitalar.model.Paciente;
import trabalhoPratico.sistemaHospitalar.model.ProfissionalSaude;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusConsulta;
import trabalhoPratico.sistemaHospitalar.repository.ConsultaRepository;
import trabalhoPratico.sistemaHospitalar.repository.PacienteRepository;
import trabalhoPratico.sistemaHospitalar.repository.ProfissionalSaudeRepository;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalSaudeRepository profissionalRepository;

    public ConsultaService(
            ConsultaRepository consultaRepository,
            PacienteRepository pacienteRepository,
            ProfissionalSaudeRepository profissionalRepository) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.profissionalRepository = profissionalRepository;
    }

    @Transactional
    public ConsultaResponse agendar(ConsultaRequest request) {
        Paciente paciente = pacienteRepository.findById(request.pacienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Paciente não encontrado: " + request.pacienteId() + "."));
        ProfissionalSaude profissional = profissionalRepository.findById(request.profissionalId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Profissional não encontrado: " + request.profissionalId() + "."));

        Consulta consulta = switch (request.modalidade()) {
            case ROTINA -> criarConsultaRotina(request, paciente, profissional);
            case ESPECIALIZADA -> criarConsultaEspecializada(request, paciente, profissional);
        };
        return ConsultaResponse.de(consultaRepository.saveAndFlush(consulta));
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> listar(StatusConsulta status) {
        List<Consulta> consultas = status == null
                ? consultaRepository.findAll()
                : consultaRepository.findByStatus(status);
        return consultas.stream().map(ConsultaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ConsultaResponse buscarPorId(Long id) {
        return ConsultaResponse.de(buscarEntidade(id));
    }

    @Transactional
    public ConsultaResponse confirmar(Long id) {
        Consulta consulta = buscarEntidade(id);
        consulta.confirmar();
        return salvar(consulta);
    }

    @Transactional
    public ConsultaResponse iniciar(Long id) {
        Consulta consulta = buscarEntidade(id);
        consulta.iniciar();
        return salvar(consulta);
    }

    @Transactional
    public ConsultaResponse concluir(Long id, ConclusaoConsultaRequest request) {
        Consulta consulta = buscarEntidade(id);
        consulta.concluir(request.observacoes());
        return salvar(consulta);
    }

    @Transactional
    public ConsultaResponse cancelar(Long id) {
        Consulta consulta = buscarEntidade(id);
        consulta.cancelar();
        return salvar(consulta);
    }

    @Transactional
    public ConsultaResponse registrarFalta(Long id) {
        Consulta consulta = buscarEntidade(id);
        consulta.registrarFalta();
        return salvar(consulta);
    }

    @Transactional
    public ConsultaResponse remarcar(Long id, RemarcacaoConsultaRequest request) {
        Consulta consulta = buscarEntidade(id);
        consulta.remarcar(request.data(), request.horario());
        return salvar(consulta);
    }

    private Consulta criarConsultaRotina(
            ConsultaRequest request, Paciente paciente, ProfissionalSaude profissional) {
        if (!(profissional instanceof MedicoClinicoGeral clinico)) {
            throw new RegraDeNegocioException(
                    "A consulta de rotina deve ser feita por um médico clínico geral.");
        }
        return new ConsultaRotina(
                paciente, clinico, request.data(), request.horario(), request.motivo());
    }

    private Consulta criarConsultaEspecializada(
            ConsultaRequest request, Paciente paciente, ProfissionalSaude profissional) {
        if (!(profissional instanceof MedicoEspecialista especialista)) {
            throw new RegraDeNegocioException(
                    "A consulta especializada deve ser feita por um médico especialista.");
        }
        return new ConsultaEspecializada(
                paciente,
                especialista,
                request.data(),
                request.horario(),
                request.motivo(),
                request.especialidade());
    }

    private Consulta buscarEntidade(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada: " + id + "."));
    }

    private ConsultaResponse salvar(Consulta consulta) {
        return ConsultaResponse.de(consultaRepository.saveAndFlush(consulta));
    }
}
