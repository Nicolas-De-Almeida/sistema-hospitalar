package trabalhoPratico.sistemaHospitalar.dto;

import java.util.List;

import trabalhoPratico.sistemaHospitalar.model.Cirurgiao;
import trabalhoPratico.sistemaHospitalar.model.MedicoClinicoGeral;
import trabalhoPratico.sistemaHospitalar.model.MedicoEspecialista;
import trabalhoPratico.sistemaHospitalar.model.ProfissionalSaude;
import trabalhoPratico.sistemaHospitalar.model.enums.CategoriaProfissional;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;

public record ProfissionalResponse(
        Long id,
        String nome,
        String telefone,
        String email,
        String registroProfissional,
        CategoriaProfissional categoria,
        Especialidade especialidade,
        String areaCirurgica,
        boolean ativo,
        List<DisponibilidadeResponse> disponibilidades) {

    public static ProfissionalResponse de(ProfissionalSaude profissional) {
        String areaCirurgica = profissional instanceof Cirurgiao cirurgiao
                ? cirurgiao.getAreaCirurgica()
                : null;
        List<DisponibilidadeResponse> disponibilidades = profissional.getDisponibilidades().stream()
                .map(DisponibilidadeResponse::de)
                .toList();

        return new ProfissionalResponse(
                profissional.getId(),
                profissional.getNome(),
                profissional.getTelefone(),
                profissional.getEmail(),
                profissional.getRegistroProfissional(),
                categoriaDe(profissional),
                profissional.getEspecialidade(),
                areaCirurgica,
                profissional.isAtivo(),
                disponibilidades);
    }

    private static CategoriaProfissional categoriaDe(ProfissionalSaude profissional) {
        if (profissional instanceof MedicoClinicoGeral) {
            return CategoriaProfissional.CLINICO_GERAL;
        }
        if (profissional instanceof MedicoEspecialista) {
            return CategoriaProfissional.ESPECIALISTA;
        }
        if (profissional instanceof Cirurgiao) {
            return CategoriaProfissional.CIRURGIAO;
        }
        throw new IllegalArgumentException("Categoria de profissional desconhecida.");
    }
}
