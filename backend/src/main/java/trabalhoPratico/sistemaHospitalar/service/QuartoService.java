package trabalhoPratico.sistemaHospitalar.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabalhoPratico.sistemaHospitalar.dto.QuartoRequest;
import trabalhoPratico.sistemaHospitalar.dto.QuartoResponse;
import trabalhoPratico.sistemaHospitalar.exception.ConflitoException;
import trabalhoPratico.sistemaHospitalar.exception.RecursoNaoEncontradoException;
import trabalhoPratico.sistemaHospitalar.model.Quarto;
import trabalhoPratico.sistemaHospitalar.repository.QuartoRepository;

@Service
public class QuartoService {

    private final QuartoRepository quartoRepository;

    public QuartoService(QuartoRepository quartoRepository) {
        this.quartoRepository = quartoRepository;
    }

    @Transactional
    public QuartoResponse cadastrar(QuartoRequest request) {
        String numero = normalizarNumero(request.numero());
        validarNumeroDisponivel(numero, null);
        Quarto quarto = new Quarto(numero, request.andar(), request.capacidadeMaxima(), request.tipo());
        return QuartoResponse.de(quartoRepository.saveAndFlush(quarto));
    }

    @Transactional(readOnly = true)
    public List<QuartoResponse> listar() {
        return quartoRepository.findAll().stream()
                .map(QuartoResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<QuartoResponse> listarDisponiveis() {
        return quartoRepository.findAll().stream()
                .filter(Quarto::temVaga)
                .map(QuartoResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuartoResponse buscarPorId(Long id) {
        return QuartoResponse.de(buscarEntidade(id));
    }

    @Transactional
    public QuartoResponse atualizar(Long id, QuartoRequest request) {
        Quarto quarto = buscarEntidade(id);
        String numero = normalizarNumero(request.numero());
        validarNumeroDisponivel(numero, id);
        quarto.atualizarCaracteristicas(
                numero, request.andar(), request.capacidadeMaxima(), request.tipo());
        return QuartoResponse.de(quartoRepository.saveAndFlush(quarto));
    }

    private Quarto buscarEntidade(Long id) {
        return quartoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Quarto não encontrado: " + id + "."));
    }

    private void validarNumeroDisponivel(String numero, Long idAtual) {
        boolean duplicado = idAtual == null
                ? quartoRepository.existsByNumero(numero)
                : quartoRepository.existsByNumeroAndIdNot(numero, idAtual);
        if (duplicado) {
            throw new ConflitoException("Já existe um quarto cadastrado com este número.");
        }
    }

    private static String normalizarNumero(String numero) {
        return numero.trim().toUpperCase();
    }
}
