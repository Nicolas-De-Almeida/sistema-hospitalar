# Guia do login (Spring Boot + React)

Este guia explica, passo a passo, como o login do Sistema Hospitalar foi feito. A ideia é você conseguir ler, entender e refazer os arquivos na mão. O código completo está nos arquivos do projeto; aqui eu explico o porquê de cada parte.

---

## 1. O que o login faz

1. O usuário abre o sistema e cai na tela `/login`.
2. Digita e-mail e senha e clica em **Entrar**.
3. O React manda os dois para o back end (`POST /api/auth/login`).
4. O Spring Boot procura o usuário no banco pelo e-mail, compara a senha e responde.
5. Se der certo, o React guarda quem logou e abre o painel. Se der errado, mostra a mensagem de erro.

**Escopo atual:** é um login simples, sem token (JWT) e sem Spring Security completo. Serve para a sprint e para aprender a base. Os próximos passos estão na seção 10.

---

## 2. Arquivos criados e alterados

### Back end (`src/main/java/trabalhoPratico/sistemaHospitalar/`)

| Arquivo | Para que serve |
|---|---|
| `model/Usuario.java` | Classe que vira a tabela `usuarios` no banco |
| `model/Perfil.java` | Tipos de usuário (administrador, profissional de saúde, recepcionista) |
| `repository/UsuarioRepository.java` | Busca e salva usuários no banco |
| `dto/LoginRequest.java` | O que o front envia (e-mail e senha) |
| `dto/LoginResponse.java` | O que o back devolve (id, nome, e-mail, função) |
| `dto/ErroResponse.java` | Formato das mensagens de erro |
| `service/AuthService.java` | Regra do login: acha o usuário e confere a senha |
| `controller/AuthController.java` | Endpoint `POST /api/auth/login` |
| `exception/CredenciaisInvalidasException.java` | Erro de "e-mail ou senha inválidos" |
| `exception/GlobalExceptionHandler.java` | Transforma os erros em respostas HTTP |
| `config/PasswordConfig.java` | Cria o objeto que faz o hash das senhas (BCrypt) |
| `config/DataSeeder.java` | Cria um usuário de teste ao iniciar |

Também mudaram: `pom.xml`, `application.properties` e dois arquivos novos de configuração (`application-dev.properties` e `application-mysql.properties`). Tem ainda um teste em `src/test/.../service/AuthServiceTest.java`.

### Front end (`frontend/src/`)

| Arquivo | Para que serve |
|---|---|
| `pages/Login.jsx` | A tela de login |
| `services/authService.js` | Chama o back end e guarda o usuário logado |
| `components/RotaProtegida.jsx` | Barra quem não está logado |
| `App.jsx` (alterado) | Rota `/login` fora do Layout e o resto protegido |
| `components/topbar.jsx` (alterado) | Mostra o usuário real e tem o botão de sair |
| `vite.config.js` (alterado) | Proxy para o back end |

---

## 3. Conceitos de Spring Boot (leia antes do código)

### O que é o Spring Boot
É um framework Java que monta uma aplicação web quase pronta. Ele sobe um servidor (Tomcat), liga o banco e conecta as suas classes entre si. Você escreve só a parte do negócio.

### A classe `SistemaHospitalarApplication`
Já existia no projeto. A anotação `@SpringBootApplication` faz o Spring varrer todos os pacotes abaixo de `trabalhoPratico.sistemaHospitalar` procurando classes anotadas (`@RestController`, `@Service`, `@Entity`...). **Por isso todas as classes novas precisam ficar dentro desse pacote ou de subpacotes dele.**

### Camadas
O projeto usa arquitetura em camadas, como pede o trabalho. Cada camada tem uma responsabilidade:

```
Front (React)
   │  HTTP (JSON)
   ▼
Controller   → recebe a requisição e devolve a resposta. Não tem regra de negócio.
   │
   ▼
Service      → regras de negócio (no login: conferir a senha).
   │
   ▼
Repository   → conversa com o banco.
   │
   ▼
Banco de dados (H2 ou MySQL)
```

### Injeção de dependência
Repare que ninguém escreve `new AuthService(...)` no projeto. O Spring cria os objetos (chamados de **beans**) e entrega para quem precisa, pelo construtor:

```java
public AuthController(AuthService authService) {
    this.authService = authService;
}
```

Quando o Spring vai criar o `AuthController`, ele vê que precisa de um `AuthService`, cria esse primeiro e passa. As anotações `@Service`, `@RestController`, `@Component` e `@Configuration` dizem ao Spring: "essa classe é sua, gerencie ela".

### DTO
DTO (Data Transfer Object) é uma classe só para trafegar dados entre front e back. Não usamos a própria `Usuario` na resposta porque ela tem o `senhaHash`, que nunca pode sair do servidor. Os DTOs aqui são `record`, um recurso do Java para classes que só carregam dados (já vem com construtor, getters e `equals`).

---

## 4. Passo a passo do back end

Siga esta ordem. Cada passo depende do anterior.

### Passo 1: dependências no `pom.xml`
Foram adicionadas duas:

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

- `spring-security-crypto`: só a parte de criptografia do Spring Security (o BCrypt). Não ativa o bloqueio automático de rotas, então não atrapalha o resto do projeto.
- `h2`: um banco que roda dentro da memória do próprio programa. Serve para você testar sem instalar o MySQL.

### Passo 2: configuração do banco
O projeto já tinha o driver do MySQL e o JPA, mas o `application.properties` não tinha o endereço do banco. Sem isso, o Spring normalmente não consegue iniciar. Agora existem três arquivos:

- `application.properties`: define qual **perfil** está ativo (`dev`).
- `application-dev.properties`: usa o H2 em memória. É o padrão.
- `application-mysql.properties`: usa o MySQL do grupo.

Perfil é um jeito de ter configurações diferentes para ambientes diferentes. O Spring lê `application.properties` e depois o `application-{perfil}.properties` do perfil ativo.

Linhas importantes do `dev`:

- `spring.jpa.hibernate.ddl-auto=create-drop`: o Hibernate cria as tabelas a partir das classes `@Entity` ao subir e apaga ao parar.
- `spring.jpa.show-sql=true`: mostra no terminal o SQL que ele executa. Ajuda muito a entender o que acontece.
- `app.seed-admin=true`: liga o `DataSeeder` (passo 11).

### Passo 3: o enum `Perfil`
Lista fechada de tipos de usuário. Cada um tem uma descrição em texto que o front mostra na topbar ("Administrador").

### Passo 4: a entidade `Usuario`
```java
@Entity
@Table(name = "usuarios")
public class Usuario { ... }
```

- `@Entity`: diz ao JPA que essa classe vira uma tabela.
- `@Table(name = "usuarios")`: nome da tabela.
- `@Id` e `@GeneratedValue(strategy = GenerationType.IDENTITY)`: chave primária com auto incremento.
- `@Column(nullable = false, unique = true)` no e-mail: não aceita vazio nem repetido. É uma proteção do próprio banco.
- `@Enumerated(EnumType.STRING)`: grava o nome do perfil ("ADMINISTRADOR") em vez do número. Se alguém mudar a ordem do enum, os dados antigos não quebram.
- O campo se chama `senhaHash` porque guardamos só o **hash** da senha, nunca ela em texto. Se alguém vazar o banco, não vê as senhas.
- O construtor vazio `protected` é exigido pelo JPA. O construtor com parâmetros é o que a gente usa no código.

### Passo 5: o `UsuarioRepository`
```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
```

É uma **interface**, sem implementação. O Spring Data gera a implementação sozinho. Ao estender `JpaRepository<Usuario, Long>` você já ganha `save`, `findById`, `findAll`, `count`, `delete`... (`Long` é o tipo do id).

O método `findByEmail` funciona só pelo nome: o Spring lê "find by Email" e monta o `SELECT * FROM usuarios WHERE email = ?`. O `Optional` serve para o caso de não existir ninguém com aquele e-mail, sem precisar tratar `null`.

### Passo 6: os DTOs
`LoginRequest` tem validação:

```java
@NotBlank(message = "Informe o e-mail.")
@Email(message = "E-mail inválido.")
String email
```

Essas anotações (do `spring-boot-starter-validation`, que já estava no projeto) só funcionam quando o controller usa `@Valid` (passo 9).

### Passo 7: a exceção
`CredenciaisInvalidasException` é uma exceção nossa. Ela existe para o service poder dizer "login falhou" sem saber nada sobre HTTP.

### Passo 8: o `AuthService`
Este é o coração do login:

1. Limpa o e-mail (`trim` e minúsculas).
2. Busca no banco com `findByEmail`. Se não achar, lança a exceção.
3. Compara a senha digitada com o hash usando `passwordEncoder.matches(...)`.
4. Se não bater, lança a mesma exceção.
5. Se bater, devolve o `LoginResponse`.

Dois detalhes que valem a pena:

- **Mesma mensagem para e-mail errado e senha errada.** Assim quem tenta invadir não consegue descobrir quais e-mails existem no sistema.
- **Nunca se compara senha com `equals`.** O BCrypt gera um hash diferente a cada vez (ele embute um "sal" aleatório), então só o `matches` consegue conferir.

### Passo 9: o `AuthController`
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.autenticar(request));
    }
}
```

- `@RestController`: a classe responde HTTP e devolve JSON.
- `@RequestMapping("/api/auth")`: prefixo de todos os endpoints da classe.
- `@PostMapping("/login")`: junta tudo em `POST /api/auth/login`.
- `@RequestBody`: pega o JSON do corpo e transforma em `LoginRequest`.
- `@Valid`: roda as validações do DTO antes de entrar no método.
- `ResponseEntity.ok(...)`: resposta com status 200.

O controller só recebe e repassa. Ele não confere senha nem fala com o banco.

### Passo 10: o `GlobalExceptionHandler`
`@RestControllerAdvice` é um "pega-erros" global. Ele traduz exceções em respostas HTTP:

- `CredenciaisInvalidasException` vira **401** com `{"mensagem": "E-mail ou senha inválidos."}`.
- `MethodArgumentNotValidException` (falha do `@Valid`) vira **400** com a mensagem da validação.

Assim o front sempre recebe um JSON com o campo `mensagem`.

### Passo 11: `PasswordConfig` e `DataSeeder`
`PasswordConfig` tem um método `@Bean` que cria o `BCryptPasswordEncoder`. É assim que você registra no Spring um objeto de uma biblioteca que não é anotada com `@Component`. Depois é só pedir `PasswordEncoder` no construtor.

`DataSeeder` implementa `CommandLineRunner`: o método `run` é executado uma vez quando o sistema termina de subir. Se a tabela estiver vazia, ele cria o usuário de teste. A anotação `@ConditionalOnProperty` faz a classe só existir quando `app.seed-admin=true`.

**Usuário de teste:** `admin@hospital.com` / `admin123`.

---

## 5. Como rodar

### Back end
Na pasta raiz do projeto (onde está o `pom.xml`):

```bash
./mvnw spring-boot:run
```

No Windows: `mvnw.cmd spring-boot:run`. Também dá para rodar a classe `SistemaHospitalarApplication` pela IDE.

O projeto está configurado para Java 25. Se der erro de versão, confira a sua JDK.

Quando subir, o terminal mostra algo como `Started SistemaHospitalarApplication` e o SQL de criação da tabela `usuarios`. O servidor fica em `http://localhost:8080`.

### Testando só o back end (sem o front)
Com o servidor rodando, em outro terminal:

```bash
# Login correto: deve devolver 200 com nome, e-mail e função
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@hospital.com","senha":"admin123"}'

# Senha errada: deve devolver 401
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@hospital.com","senha":"errada"}'

# E-mail vazio: deve devolver 400
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"","senha":"123"}'
```

Testar o back sozinho é uma boa prática: se funcionar no `curl`, qualquer problema depois está no front.

### Front end
Em outro terminal:

```bash
cd frontend
npm install
npm run dev
```

Abra o endereço que o Vite mostrar (normalmente `http://localhost:5173`). Ele deve redirecionar para `/login`.

### Testes automáticos
```bash
./mvnw test
```

O `AuthServiceTest` testa três situações: login correto, senha errada e e-mail inexistente. Ele usa o Mockito para simular o repository, então não precisa de banco.

---

## 6. Como o front funciona

### `authService.js`
Concentra tudo que tem a ver com login:

- `login(email, senha)`: faz o `fetch` para `/api/auth/login`. Se o status não for 2xx, lança um erro com a `mensagem` que veio do back. Se der certo, salva o usuário no `localStorage`.
- `logout()`: apaga o usuário salvo.
- `getUsuarioLogado()` e `estaLogado()`: leitura do que está salvo.

Deixar isso fora dos componentes é de propósito: o `Login.jsx` só cuida da tela.

### `Login.jsx`
- Cada campo é um **estado** (`useState`): `email`, `senha`, `erro`, `carregando`, `mostrarSenha`.
- No `handleSubmit`, o `e.preventDefault()` impede o navegador de recarregar a página. Depois chama o `login`, e se der certo vai para `/` com `navigate`.
- O `try / catch / finally` mostra o erro na tela e desliga o "Entrando..." de qualquer jeito.
- O visual segue o resto do projeto (cyan, slate, `rounded-2xl`, mesmos inputs e botão).

### `RotaProtegida.jsx`
Se não tem ninguém logado, devolve `<Navigate to="/login" />`. Se tem, mostra os filhos.

### `App.jsx`
Antes, o `Layout` (sidebar e topbar) envolvia todas as rotas. Agora:

- `/login` fica sozinha, sem sidebar nem topbar.
- `/*` (qualquer outra rota) passa pela `RotaProtegida` e depois pelo `Layout`.

### `topbar.jsx`
O nome e a função do usuário não são mais fixos, vêm do `getUsuarioLogado()`. Foi adicionado um botão de sair que chama `logout()` e volta para `/login`.

### O proxy do Vite
O front roda na porta 5173 e o back na 8080. O navegador trata isso como "sites diferentes" e bloqueia a chamada (política de CORS). Para evitar isso em desenvolvimento, o `vite.config.js` repassa tudo que começar com `/api` para `http://localhost:8080`. Por isso o `fetch` usa `/api/auth/login` e não o endereço completo. **Você precisa reiniciar o `npm run dev` depois de mexer no `vite.config.js`.**

---

## 7. Usando o MySQL

Com o MySQL do grupo instalado e rodando:

1. Em `application.properties`, troque `spring.profiles.active=dev` por `spring.profiles.active=mysql`.
2. Defina usuário e senha do banco. O arquivo lê as variáveis `DB_USER` (padrão `root`) e `DB_PASSWORD` (padrão vazio). Exemplo no terminal:
   ```bash
   DB_USER=root DB_PASSWORD=suasenha ./mvnw spring-boot:run
   ```
3. O `createDatabaseIfNotExist=true` na URL cria o banco `sistema_hospitalar` se ele não existir.

Com `ddl-auto=update`, as tabelas são criadas/atualizadas e os dados ficam salvos entre execuções. **Não coloque a senha do banco direto no arquivo**, porque ele vai para o GitHub.

---

## 8. Erros comuns

| Sintoma | Causa provável |
|---|---|
| `Failed to determine a suitable driver class` | Sem perfil ativo ou sem URL do banco. Confira o `application.properties`. |
| `Access denied for user` | Usuário/senha do MySQL errados. |
| Tela de login mostra "Não foi possível conectar ao servidor" | Back end desligado, ou você não reiniciou o `npm run dev` depois de alterar o `vite.config.js`. |
| 404 em `/api/auth/login` | Classe fora do pacote `trabalhoPratico.sistemaHospitalar` (o Spring não encontra). |
| 401 com e-mail e senha certos | Banco MySQL já tinha usuário com outra senha (o seeder só cria se a tabela estiver vazia). |
| `Port 8080 was already in use` | Outra execução do back ainda aberta. |

---

## 9. Atenção: maiúsculas e minúsculas nos imports do front

Alguns imports do front não batem com o nome real do arquivo:

- `App.jsx` importa `./components/Layout`, mas o arquivo é `layout.jsx`.
- `layout.jsx` importa `./SideBar` e `./TopBar`, mas os arquivos são `sidebar.jsx` e `topbar.jsx`.

No Mac e no Windows isso funciona, porque o sistema de arquivos ignora maiúsculas. Em Linux (por exemplo, num servidor ou numa pipeline de CI) o projeto não compila. A correção é renomear os arquivos ou ajustar os imports para os nomes exatos. Vale combinar com o grupo.

---

## 10. Limitações e próximos passos

O login atual funciona, mas tem limites que você deve saber explicar:

1. **O back end não "lembra" quem logou.** Ele confere a senha e responde, mas os outros endpoints (pacientes, consultas...) não verificam login. Quem souber a URL consegue chamar a API direto. A proteção das telas no React é só visual.
2. **O estado de logado fica no `localStorage`**, sem expiração.
3. **Não há cadastro de usuário.** Só existe o usuário criado pelo `DataSeeder`.
4. **Não há bloqueio por perfil.** O `Perfil` existe, mas ainda não restringe nada.

Caminho natural para resolver:

1. **Spring Security + JWT:** o back gera um token ao logar, o front envia esse token em todas as chamadas (`Authorization: Bearer ...`) e o back valida em cada endpoint. Resolve os itens 1 e 2.
2. **Endpoint para cadastrar usuários** (só para administrador), usando o mesmo `PasswordEncoder`.
3. **Autorização por perfil**, para limitar o que cada tipo de usuário pode fazer.
4. Remover o `DataSeeder` do que for para uso real.

---

## 11. Perguntas que podem aparecer na apresentação

- **Por que guardar hash e não a senha?** Para que um vazamento do banco não exponha as senhas. O hash não se desfaz.
- **Por que o BCrypt e não um hash simples (MD5, SHA-1)?** O BCrypt é lento de propósito e usa sal, o que dificulta ataques por força bruta e por tabelas prontas.
- **Por que dividir em controller, service e repository?** Cada camada tem uma responsabilidade só, o que facilita testar e mudar. O `AuthServiceTest`, por exemplo, testa a regra sem subir servidor nem banco.
- **Por que usar DTO em vez da entidade?** Para controlar o que sai e o que entra. A entidade tem o `senhaHash`.
- **Por que a mensagem de erro é igual para e-mail e senha errados?** Para não revelar quais e-mails existem no sistema.
