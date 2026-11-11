package trabalhoPratico.sistemaHospitalar.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabalhoPratico.sistemaHospitalar.dto.ConclusaoEmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.dto.EmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.dto.EmergenciaResponse;
import trabalhoPratico.sistemaHospitalar.dto.InicioEmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.dto.PrioridadeEmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.model.AtendimentoEmergencia;
import trabalhoPratico.sistemaHospitalar.model.Paciente;
import trabalhoPratico.sistemaHospitalar.model.ProfissionalSaude;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusEmergencia;
import trabalhoPratico.sistemaHospitalar.repository.AtendimentoEmergenciaRepository;
import trabalhoPratico.sistemaHospitalar.repository.PacienteRepository;
import trabalhoPratico.sistemaHospitalar.repository.ProfissionalSaudeRepository;

@Service
public class EmergenciaService {

    private final AtendimentoEmergenciaRepository emergenciaRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalSaudeRepository profissionalRepository;

    public EmergenciaService(
            AtendimentoEmergenciaRepository emergenciaRepository,
            PacienteRepository pacienteRepository,
            ProfissionalSaudeRepository profissionalRepository) {
        this.emergenciaRepository = emergenciaRepository;
        this.pacienteRepository = pacienteRepository;
        this.profissionalRepository = profissionalRepository;
    }

    @Transactional
    public EmergenciaResponse registrar(EmergenciaRequest request) {
        Paciente paciente = pacienteRepository.findById(request.pacienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Paciente não encontrado: " + request.pacienteId() + "."));
        AtendimentoEmergencia emergencia = new AtendimentoEmergencia(
                paciente, request.prioridade(), request.queixaPrincipal());
        return EmergenciaResponse.de(emergenciaRepository.saveAndFlush(emergencia));
    }

    @Transactional(readOnly = true)
    public List<EmergenciaResponse> listar(StatusEmergencia status) {
        List<AtendimentoEmergencia> atendimentos = status == null
                ? emergenciaRepository.findAll()
                : emergenciaRepository.findByStatus(status);
        return atendimentos.stream().map(EmergenciaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<EmergenciaResponse> listarFila() {
        return emergenciaRepository.findByStatus(StatusEmergencia.AGUARDANDO_AVALIACAO).stream()
                .sorted(Comparator
                        .comparingInt((AtendimentoEmergencia item) -> item.getPrioridade().getNivel())
                        .reversed()
                        .thenComparing(AtendimentoEmergencia::getCriadoEm))
                .map(EmergenciaResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmergenciaResponse buscarPorId(Long id) {
        return EmergenciaResponse.de(buscarEntidade(id));
    }

    @Transactional
    public EmergenciaResponse iniciarAvaliacao(Long id, InicioEmergenciaRequest request) {
        AtendimentoEmergencia emergencia = buscarEntidade(id);
        ProfissionalSaude profissional = profissionalRepository.findById(request.profissionalId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Profissional não encontrado: " + request.profissionalId() + "."));
        emergencia.iniciarAvaliacao(profissional);
        return salvar(emergencia);
    }

    @Transactional
    public EmergenciaResponse reclassificar(Long id, PrioridadeEmergenciaRequest request) {
        AtendimentoEmergencia emergencia = buscarEntidade(id);
        emergencia.setPrioridade(request.prioridade());
        return salvar(emergencia);
    }

    @Transactional
    public EmergenciaResponse concluir(Long id, ConclusaoEmergenciaRequest request) {
        AtendimentoEmergencia emergencia = buscarEntidade(id);
        emergencia.concluir(request.observacoes());
        return salvar(emergencia);
    }

    private AtendimentoEmergencia buscarEntidade(Long id) {
        return emergenciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Atendimento de emergência não encontrado: " + id + "."));
    }

    private EmergenciaResponse salvar(AtendimentoEmergencia emergencia) {
        return EmergenciaResponse.de(emergenciaRepository.saveAndFlush(emergencia));
    }
}
