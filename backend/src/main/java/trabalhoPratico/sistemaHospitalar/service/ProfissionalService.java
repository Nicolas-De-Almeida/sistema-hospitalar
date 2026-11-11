package trabalhoPratico.sistemaHospitalar.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabalhoPratico.sistemaHospitalar.dto.DisponibilidadeRequest;
import trabalhoPratico.sistemaHospitalar.dto.DisponibilidadeResponse;
import trabalhoPratico.sistemaHospitalar.dto.ProfissionalRequest;
import trabalhoPratico.sistemaHospitalar.dto.ProfissionalResponse;
import trabalhoPratico.sistemaHospitalar.dto.StatusProfissionalRequest;
import trabalhoPratico.sistemaHospitalar.exception.ConflitoException;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.Cirurgiao;
import trabalhoPratico.sistemaHospitalar.model.DisponibilidadeAtendimento;
import trabalhoPratico.sistemaHospitalar.model.MedicoClinicoGeral;
import trabalhoPratico.sistemaHospitalar.model.MedicoEspecialista;
import trabalhoPratico.sistemaHospitalar.model.ProfissionalSaude;
import trabalhoPratico.sistemaHospitalar.model.enums.CategoriaProfissional;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;
import trabalhoPratico.sistemaHospitalar.repository.DisponibilidadeAtendimentoRepository;
import trabalhoPratico.sistemaHospitalar.repository.ProfissionalSaudeRepository;

@Service
public class ProfissionalService {

    private final ProfissionalSaudeRepository profissionalRepository;
    private final DisponibilidadeAtendimentoRepository disponibilidadeRepository;

    public ProfissionalService(
            ProfissionalSaudeRepository profissionalRepository,
            DisponibilidadeAtendimentoRepository disponibilidadeRepository) {
        this.profissionalRepository = profissionalRepository;
        this.disponibilidadeRepository = disponibilidadeRepository;
    }

    @Transactional
    public ProfissionalResponse cadastrar(ProfissionalRequest request) {
        String registro = normalizarRegistro(request.registroProfissional());
        validarRegistroDisponivel(registro, null);
        ProfissionalSaude profissional = criarProfissional(request, registro);
        return ProfissionalResponse.de(profissionalRepository.saveAndFlush(profissional));
    }

    @Transactional(readOnly = true)
    public List<ProfissionalResponse> listar(Especialidade especialidade) {
        List<ProfissionalSaude> profissionais = especialidade == null
                ? profissionalRepository.findAll()
                : profissionalRepository.findByEspecialidade(especialidade);
        return profissionais.stream().map(ProfissionalResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ProfissionalResponse buscarPorId(Long id) {
        return ProfissionalResponse.de(buscarEntidade(id));
    }

    @Transactional
    public ProfissionalResponse atualizar(Long id, ProfissionalRequest request) {
        ProfissionalSaude profissional = buscarEntidade(id);
        validarCategoria(profissional, request.categoria());

        String registro = normalizarRegistro(request.registroProfissional());
        validarRegistroDisponivel(registro, id);
        profissional.setNome(request.nome());
        profissional.setTelefone(request.telefone());
        profissional.setEmail(request.email());
        profissional.setRegistroProfissional(registro);

        if (profissional instanceof MedicoEspecialista especialista) {
            especialista.setEspecialidade(request.especialidade());
        } else if (profissional instanceof Cirurgiao cirurgiao) {
            cirurgiao.setAreaCirurgica(request.areaCirurgica());
        }

        return ProfissionalResponse.de(profissionalRepository.saveAndFlush(profissional));
    }

    @Transactional
    public ProfissionalResponse alterarStatus(Long id, StatusProfissionalRequest request) {
        ProfissionalSaude profissional = buscarEntidade(id);
        profissional.setAtivo(request.ativo());
        return ProfissionalResponse.de(profissionalRepository.saveAndFlush(profissional));
    }

    @Transactional
    public DisponibilidadeResponse adicionarDisponibilidade(Long id, DisponibilidadeRequest request) {
        ProfissionalSaude profissional = buscarEntidade(id);
        DisponibilidadeAtendimento disponibilidade = profissional.adicionarDisponibilidade(
                request.diaSemana(), request.horaInicio(), request.horaFim(), request.duracaoConsultaMin());
        DisponibilidadeAtendimento disponibilidadeSalva = disponibilidadeRepository.saveAndFlush(disponibilidade);
        return DisponibilidadeResponse.de(disponibilidadeSalva);
    }

    @Transactional
    public void removerDisponibilidade(Long id, Long disponibilidadeId) {
        ProfissionalSaude profissional = buscarEntidade(id);
        DisponibilidadeAtendimento disponibilidade = profissional.getDisponibilidades().stream()
                .filter(item -> disponibilidadeId.equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Disponibilidade não encontrada para o profissional informado."));
        profissional.removerDisponibilidade(disponibilidade);
        profissionalRepository.saveAndFlush(profissional);
    }

    private ProfissionalSaude buscarEntidade(Long id) {
        return profissionalRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrado: " + id + "."));
    }

    private ProfissionalSaude criarProfissional(ProfissionalRequest request, String registro) {
        return switch (request.categoria()) {
            case CLINICO_GERAL -> new MedicoClinicoGeral(
                    request.nome(), request.telefone(), request.email(), registro);
            case ESPECIALISTA -> new MedicoEspecialista(
                    request.nome(), request.telefone(), request.email(), registro, request.especialidade());
            case CIRURGIAO -> new Cirurgiao(
                    request.nome(), request.telefone(), request.email(), registro, request.areaCirurgica());
        };
    }

    private void validarCategoria(ProfissionalSaude profissional, CategoriaProfissional categoriaSolicitada) {
        boolean corresponde = switch (categoriaSolicitada) {
            case CLINICO_GERAL -> profissional instanceof MedicoClinicoGeral;
            case ESPECIALISTA -> profissional instanceof MedicoEspecialista;
            case CIRURGIAO -> profissional instanceof Cirurgiao;
        };
        if (!corresponde) {
            throw new RegraDeNegocioException("Não é permitido alterar a categoria de um profissional cadastrado.");
        }
    }

    private void validarRegistroDisponivel(String registro, Long idAtual) {
        boolean duplicado = idAtual == null
                ? profissionalRepository.existsByRegistroProfissional(registro)
                : profissionalRepository.existsByRegistroProfissionalAndIdNot(registro, idAtual);
        if (duplicado) {
            throw new ConflitoException("Já existe um profissional cadastrado com este registro profissional.");
        }
    }

    private static String normalizarRegistro(String registro) {
        return registro.trim().toUpperCase();
    }
}
