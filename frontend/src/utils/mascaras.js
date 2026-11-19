// Formatam o texto enquanto a pessoa escreve.

// 12345678901 -> 123.456.789-01
export function mascararCpf(valor) {
    const numeros = valor.replace(/\D/g, '').slice(0, 11)
    return numeros
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
}

// 31999998888 -> (31) 99999-8888  |  3133334444 -> (31) 3333-4444
export function mascararTelefone(valor) {
    const n = valor.replace(/\D/g, '').slice(0, 11)

    if (n.length === 0) return ''
    if (n.length <= 2) return `(${n}`
    if (n.length <= 6) return `(${n.slice(0, 2)}) ${n.slice(2)}`
    if (n.length <= 10) return `(${n.slice(0, 2)}) ${n.slice(2, 6)}-${n.slice(6)}`
    return `(${n.slice(0, 2)}) ${n.slice(2, 7)}-${n.slice(7)}`
}

// 30130010 -> 30130-010
export function mascararCep(valor) {
    const n = valor.replace(/\D/g, '').slice(0, 8)
    return n.length > 5 ? `${n.slice(0, 5)}-${n.slice(5)}` : n
}