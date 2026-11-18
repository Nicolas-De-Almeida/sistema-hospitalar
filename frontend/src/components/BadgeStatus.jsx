// Etiqueta colorida com bolinha.
export default function BadgeStatus({ estilo }) {
    return (
        <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-[11px] font-semibold ${estilo.cor}`}>
        <span className={`w-1.5 h-1.5 rounded-full ${estilo.bolinha}`}></span>
            {estilo.texto}
        </span>
    )
}