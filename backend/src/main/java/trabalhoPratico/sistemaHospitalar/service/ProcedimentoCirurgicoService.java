package trabalhoPratico.sistemaHospitalar.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabalhoPratico.sistemaHospitalar.dto.ConclusaoProcedimentoRequest;
import trabalhoPratico.sistemaHospitalar.dto.ProcedimentoCirurgicoRequest;
import trabalhoPratico.sistemaHospitalar.dto.ProcedimentoCirurgicoResponse;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.Cirurgiao;
import trabalhoPratico.sistemaHospitalar.model.Paciente;
import trabalhoPratico.sistemaHospitalar.model.ProcedimentoCirurgico;
import trabalhoPratico.sistemaHospitalar.model.ProfissionalSaude;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusProcedimento;
import trabalhoPratico.sistemaHospitalar.repository.PacienteRepository;
import trabalhoPratico.sistemaHospitalar.repository.ProcedimentoCirurgicoRepository;
import trabalhoPratico.sistemaHospitalar.repository.ProfissionalSaudeRepository;

@Service
public class ProcedimentoCirurgicoService {

    private final ProcedimentoCirurgicoRepository procedimentoRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalSaudeRepository profissionalRepository;

    public ProcedimentoCirurgicoService(
            ProcedimentoCirurgicoRepository procedimentoRepository,
            PacienteRepository pacienteRepository,
            ProfissionalSaudeRepository profissionalRepository) {
        this.procedimentoRepository = procedimentoRepository;
        this.pacienteRepository = pacienteRepository;
        this.profissionalRepository = profissionalRepository;
    }

    @Transactional
    public ProcedimentoCirurgicoResponse agendar(ProcedimentoCirurgicoRequest request) {
        Paciente paciente = pacienteRepository.findById(request.pacienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Paciente não encontrado: " + request.pacienteId() + "."));
        ProfissionalSaude profissional = profissionalRepository.findById(request.cirurgiaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Profissional não encontrado: " + request.cirurgiaoId() + "."));
        if (!(profissional instanceof Cirurgiao cirurgiao)) {
            throw new RegraDeNegocioException(
                    "O procedimento cirúrgico deve ter um cirurgião responsável.");
        }

        ProcedimentoCirurgico procedimento = new ProcedimentoCirurgico(
                paciente,
                cirurgiao,
                request.dataPrevista(),
                request.horarioPrevisto(),
                request.descricao(),
                request.tipoCirurgia(),
                request.observacoesPreOperatorias());
        return ProcedimentoCirurgicoResponse.de(procedimentoRepository.saveAndFlush(procedimento));
    }

    @Transactional(readOnly = true)
    public List<ProcedimentoCirurgicoResponse> listar(StatusProcedimento status) {
        List<ProcedimentoCirurgico> procedimentos = status == null
                ? procedimentoRepository.findAll()
                : procedimentoRepository.findByStatus(status);
        return procedimentos.stream().map(ProcedimentoCirurgicoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ProcedimentoCirurgicoResponse buscarPorId(Long id) {
        return ProcedimentoCirurgicoResponse.de(buscarEntidade(id));
    }

    @Transactional
    public ProcedimentoCirurgicoResponse iniciar(Long id) {
        ProcedimentoCirurgico procedimento = buscarEntidade(id);
        procedimento.iniciar();
        return salvar(procedimento);
    }

    @Transactional
    public ProcedimentoCirurgicoResponse concluir(Long id, ConclusaoProcedimentoRequest request) {
        ProcedimentoCirurgico procedimento = buscarEntidade(id);
        procedimento.concluir(request.observacoesPosOperatorias());
        return salvar(procedimento);
    }

    @Transactional
    public ProcedimentoCirurgicoResponse cancelar(Long id) {
        ProcedimentoCirurgico procedimento = buscarEntidade(id);
        procedimento.cancelar();
        return salvar(procedimento);
    }

    private ProcedimentoCirurgico buscarEntidade(Long id) {
        return procedimentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Procedimento cirúrgico não encontrado: " + id + "."));
    }

    private ProcedimentoCirurgicoResponse salvar(ProcedimentoCirurgico procedimento) {
        return ProcedimentoCirurgicoResponse.de(procedimentoRepository.saveAndFlush(procedimento));
    }
}
