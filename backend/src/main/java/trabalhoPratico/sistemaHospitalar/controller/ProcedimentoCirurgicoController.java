package trabalhoPratico.sistemaHospitalar.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import trabalhoPratico.sistemaHospitalar.dto.ConclusaoProcedimentoRequest;
import trabalhoPratico.sistemaHospitalar.dto.ProcedimentoCirurgicoRequest;
import trabalhoPratico.sistemaHospitalar.dto.ProcedimentoCirurgicoResponse;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusProcedimento;
import trabalhoPratico.sistemaHospitalar.service.ProcedimentoCirurgicoService;

@RestController
@RequestMapping("/api/procedimentos")
public class ProcedimentoCirurgicoController {

    private final ProcedimentoCirurgicoService procedimentoService;

    public ProcedimentoCirurgicoController(ProcedimentoCirurgicoService procedimentoService) {
        this.procedimentoService = procedimentoService;
    }

    @PostMapping
    public ResponseEntity<ProcedimentoCirurgicoResponse> agendar(
            @Valid @RequestBody ProcedimentoCirurgicoRequest request) {
        ProcedimentoCirurgicoResponse response = procedimentoService.agendar(request);
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(localizacao).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProcedimentoCirurgicoResponse>> listar(
            @RequestParam(required = false) StatusProcedimento status) {
        return ResponseEntity.ok(procedimentoService.listar(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcedimentoCirurgicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(procedimentoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<ProcedimentoCirurgicoResponse> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(procedimentoService.iniciar(id));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<ProcedimentoCirurgicoResponse> concluir(
            @PathVariable Long id,
            @Valid @RequestBody ConclusaoProcedimentoRequest request) {
        return ResponseEntity.ok(procedimentoService.concluir(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ProcedimentoCirurgicoResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(procedimentoService.cancelar(id));
    }
}
