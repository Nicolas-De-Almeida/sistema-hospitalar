package trabalhoPratico.sistemaHospitalar.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabalhoPratico.sistemaHospitalar.dto.AltaInternacaoRequest;
import trabalhoPratico.sistemaHospitalar.dto.AtualizacaoInternacaoRequest;
import trabalhoPratico.sistemaHospitalar.dto.InternacaoRequest;
import trabalhoPratico.sistemaHospitalar.dto.InternacaoResponse;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.model.Internacao;
import trabalhoPratico.sistemaHospitalar.model.Paciente;
import trabalhoPratico.sistemaHospitalar.model.ProfissionalSaude;
import trabalhoPratico.sistemaHospitalar.model.Quarto;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusInternacao;
import trabalhoPratico.sistemaHospitalar.repository.InternacaoRepository;
import trabalhoPratico.sistemaHospitalar.repository.PacienteRepository;
import trabalhoPratico.sistemaHospitalar.repository.ProfissionalSaudeRepository;
import trabalhoPratico.sistemaHospitalar.repository.QuartoRepository;

@Service
public class InternacaoService {

    private final InternacaoRepository internacaoRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalSaudeRepository profissionalRepository;
    private final QuartoRepository quartoRepository;

    public InternacaoService(
            InternacaoRepository internacaoRepository,
            PacienteRepository pacienteRepository,
            ProfissionalSaudeRepository profissionalRepository,
            QuartoRepository quartoRepository) {
        this.internacaoRepository = internacaoRepository;
        this.pacienteRepository = pacienteRepository;
        this.profissionalRepository = profissionalRepository;
        this.quartoRepository = quartoRepository;
    }

    @Transactional
    public InternacaoResponse criar(InternacaoRequest request) {
        Paciente paciente = pacienteRepository.findById(request.pacienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Paciente não encontrado: " + request.pacienteId() + "."));
        ProfissionalSaude profissional = profissionalRepository.findById(request.profissionalId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Profissional não encontrado: " + request.profissionalId() + "."));
        Quarto quarto = quartoRepository.findById(request.quartoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Quarto não encontrado: " + request.quartoId() + "."));

        Internacao internacao = new Internacao(
                paciente,
                profissional,
                quarto,
                request.dataEntrada(),
                request.dataPrevistaAlta(),
                request.observacoes());
        return InternacaoResponse.de(internacaoRepository.saveAndFlush(internacao));
    }

    @Transactional(readOnly = true)
    public List<InternacaoResponse> listar(StatusInternacao status) {
        List<Internacao> internacoes = status == null
                ? internacaoRepository.findAll()
                : internacaoRepository.findByStatus(status);
        return internacoes.stream().map(InternacaoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public InternacaoResponse buscarPorId(Long id) {
        return InternacaoResponse.de(buscarEntidade(id));
    }

    @Transactional
    public InternacaoResponse atualizar(Long id, AtualizacaoInternacaoRequest request) {
        Internacao internacao = buscarEntidade(id);
        internacao.setDataPrevistaAlta(request.dataPrevistaAlta());
        internacao.setObservacoes(request.observacoes());
        return InternacaoResponse.de(internacaoRepository.saveAndFlush(internacao));
    }

    @Transactional
    public InternacaoResponse darAlta(Long id, AltaInternacaoRequest request) {
        Internacao internacao = buscarEntidade(id);
        internacao.darAlta(request.dataAlta());
        return InternacaoResponse.de(internacaoRepository.saveAndFlush(internacao));
    }

    private Internacao buscarEntidade(Long id) {
        return internacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Internação não encontrada: " + id + "."));
    }
}
