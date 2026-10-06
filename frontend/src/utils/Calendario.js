// Função auxiliar que deixa a primeira letra de cada palavra em maiúscula
const toPascalCase = (str) => {
    // Validação de segurança: se a string for nula, vazia ou indefinida, evita erros e retorna vazio
    if (!str){
        return ''
    };

    return str
        .split(' ') // Divide a frase em um array de palavras usando o espaço como separador
        .map(word => 
            // Para cada palavra: isola a 1ª letra e deixa maiúscula (charAt(0).toUpperCase()) e concatena com o restante da palavra em minúsculas (slice(1).toLowerCase())
            word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
        )
        .join(' '); // Junta o array de palavras formatadas de volta em uma única frase com espaços
};

// Função principal que gera a string da data atual formatada
export function PegarDataCalendario() {
    // 1. Instancia o objeto Date com a data e hora reais do sistema
    const currentDate = new Date();

    // 2. Extrai o dia da semana por extenso em português (ex: "segunda-feira")
    // A API nativa Intl.DateTimeFormat faz a tradução automática para pt-BR
    const rawWeekday = new Intl.DateTimeFormat('pt-BR', { weekday: 'long' }).format(currentDate);

    // 3. Extrai o nome do mês por extenso em português (ex: "outubro")
    const rawMonth = new Intl.DateTimeFormat('pt-BR', { month: 'long' }).format(currentDate);

    // 4. Aplica a formatação PascalCase para colocar as iniciais em maiúsculas
    const formattedWeekday = toPascalCase(rawWeekday);
    const formattedMonth = toPascalCase(rawMonth);

    // 5. Retorna a frase completa intercalando as variáveis calculadas e o ano (getFullYear)
    return `${formattedWeekday}, ${currentDate.getDate()} De ${formattedMonth} De ${currentDate.getFullYear()}`;
}