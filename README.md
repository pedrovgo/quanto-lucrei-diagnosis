# Quanto Lucrei - Diagnosis

Microservico Spring Boot responsavel por importar arquivos de produtos e
classificar automaticamente suas colunas usando o Jev por meio da API do
OpenRouter.

O servico recebe arquivos tabulares, identifica as colunas de nome, codigo,
descricao e custo, e transforma as linhas em objetos `Product`.

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Maven Wrapper
- Apache POI para arquivos XLS e XLSX
- Apache Commons CSV para arquivos CSV e TXT
- OpenRouter Decisions API
- Modelo `typesafe/jev-1.13`

## Requisitos

- Java 21
- Uma chave da API do OpenRouter

Verifique as versoes instaladas:

```bash
java -version
./mvnw -version
```

## Configuracao local

A chave da API nao deve ser adicionada ao codigo nem commitada no repositorio.

### IntelliJ IDEA

Em **Run > Edit Configurations**, adicione a variavel:

```text
OPENROUTER_API_KEY=sua-chave-do-openrouter
```

Opcionalmente, configure:

```text
OPENROUTER_MODEL=typesafe/jev-1.13
```

O arquivo `src/main/resources/application.yaml` usa essas variaveis:

```yaml
openrouter:
  api-key: ${OPENROUTER_API_KEY}
  model: ${OPENROUTER_MODEL:typesafe/jev-1.13}
```

### Terminal

```bash
export OPENROUTER_API_KEY="sua-chave-do-openrouter"
export OPENROUTER_MODEL="typesafe/jev-1.13"
./mvnw spring-boot:run
```

Nunca coloque uma chave real no `application.yaml`, no codigo-fonte ou em
commits.

## Execucao

Para iniciar a aplicacao:

```bash
./mvnw spring-boot:run
```

Para executar os testes:

```bash
./mvnw test
```

## API

### Importar produtos

```http
POST /api/products/import
Content-Type: multipart/form-data
```

O campo do formulario deve se chamar `file`.

Exemplo com cURL:

```bash
curl --location 'http://localhost:8080/api/products/import' \
  --form 'file=@/caminho/para/produtos.xlsx'
```

Formatos aceitos:

- `.csv`
- `.txt`
- `.xls`
- `.xlsx`

O arquivo deve conter dados tabulares. A primeira linha e tratada como
cabecalho e as linhas seguintes como dados.

Exemplo de resposta:

```json
[
  {
    "name": "Shampoo Cachos",
    "code": "789001",
    "description": "Shampoo hidratante 500 ml",
    "cost": 18.50
  }
]
```

## Revisao de arquivos nao confiaveis

O resultado do Jev e considerado confiavel quando as colunas de nome e custo
sao identificadas com confianca minima de `0.75`.

Quando a classificacao nao atinge esse criterio, o arquivo nao e transformado
em produtos. Ele e encaminhado para
`UnreliableProductFileHandlerImpl`, que atualmente registra o caso no log.

O ponto de persistencia do arquivo para analise manual esta marcado com
`TODO` nessa classe.

## Arquitetura

```text
product
├── api
├── application
│   ├── assembler
│   ├── model
│   ├── port
│   │   ├── in
│   │   └── out
│   └── service
├── domain
├── exception
└── infrastructure
    ├── file
    ├── openrouter
    └── review
```

As regras da aplicacao dependem de portas, enquanto as integracoes com
arquivos e OpenRouter ficam na infraestrutura.
