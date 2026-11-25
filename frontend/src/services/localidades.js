export const UFS = [
    'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG', 'PA', 'PB',
    'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO',
]

// Busca o endereço de um CEP
export async function buscarEnderecoPorCep(cep) {
    const numeros = cep.replace(/\D/g, '')
    const resposta = await fetch(`https://viacep.com.br/ws/${numeros}/json/`)
    if (!resposta.ok) throw new Error('Não foi possível consultar o CEP.')

    const dados = await resposta.json()
    if (dados.erro) return null

    return {
        logradouro: dados.logradouro ?? '',
        bairro: dados.bairro ?? '',
        cidade: dados.localidade ?? '',
        uf: dados.uf ?? '',
    }
}

// Guarda as cidades já baixadas (trocar de UF e voltar não baixa de novo)
const cacheCidades = new Map()

// Nomes das cidades de uma UF, em ordem alfabética
export async function listarCidades(uf) {
    if (cacheCidades.has(uf)) return cacheCidades.get(uf)

    const resposta = await fetch(`https://servicodados.ibge.gov.br/api/v1/localidades/estados/${uf}/municipios?orderBy=nome`)
    if (!resposta.ok) throw new Error('Não foi possível carregar as cidades.')

    const cidades = (await resposta.json()).map((cidade) => cidade.nome)
    cacheCidades.set(uf, cidades)
    return cidades
}