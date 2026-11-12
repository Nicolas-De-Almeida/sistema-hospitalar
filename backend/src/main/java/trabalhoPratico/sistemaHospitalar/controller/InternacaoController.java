package trabalhoPratico.sistemaHospitalar.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import trabalhoPratico.sistemaHospitalar.dto.AltaInternacaoRequest;
import trabalhoPratico.sistemaHospitalar.dto.AtualizacaoInternacaoRequest;
import trabalhoPratico.sistemaHospitalar.dto.InternacaoRequest;
import trabalhoPratico.sistemaHospitalar.dto.InternacaoResponse;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusInternacao;
import trabalhoPratico.sistemaHospitalar.service.InternacaoService;

@RestController
@RequestMapping("/api/internacoes")
public class InternacaoController {

    private final InternacaoService internacaoService;

    public InternacaoController(InternacaoService internacaoService) {
        this.internacaoService = internacaoService;
    }

    @PostMapping
    public ResponseEntity<InternacaoResponse> criar(@Valid @RequestBody InternacaoRequest request) {
        InternacaoResponse response = internacaoService.criar(request);
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(localizacao).body(response);
    }

    @GetMapping
    public ResponseEntity<List<InternacaoResponse>> listar(
            @RequestParam(required = false) StatusInternacao status) {
        return ResponseEntity.ok(internacaoService.listar(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternacaoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(internacaoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InternacaoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizacaoInternacaoRequest request) {
        return ResponseEntity.ok(internacaoService.atualizar(id, request));
    }

    @PatchMapping("/{id}/alta")
    public ResponseEntity<InternacaoResponse> darAlta(
            @PathVariable Long id,
            @Valid @RequestBody AltaInternacaoRequest request) {
        return ResponseEntity.ok(internacaoService.darAlta(id, request));
    }
}
