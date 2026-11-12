package trabalhoPratico.sistemaHospitalar.exception;

// Lançada sempre que uma regra de negócio é violada
// (exemplo: quarto lotado, profissional ocupado, mudança de status inválida).
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}