{/*Permite pegar a primeira letra do nome e sobrenome do usuário logado para usar como "foto" de perfil*/}
export function getIniciais(nomeCompleto) {
  if (!nomeCompleto) {
    return ''
  };

  const nomes = nomeCompleto.trim().split(' ');

  if (nomes.length === 1){
    return nomes[0][0].toUpperCase()
  };

  return (nomes[0][0] + nomes[nomes.length - 1][0]).toUpperCase();
}