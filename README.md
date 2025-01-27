
---

# Email Service API

Este projeto é uma aplicação RESTful desenvolvida como parte do desafio técnico da Viasoft. O objetivo principal é processar o envio de e-mails adaptados para plataformas como AWS e OCI.

## Funcionalidades

- Recebe requisições HTTP para envio de e-mails.
- Adapta o objeto de entrada para diferentes plataformas (AWS ou OCI) com base na configuração.
- Valida os dados do e-mail antes de processar.
- Serializa o objeto para JSON e exibe no console.
- Registra os logs de auditoria no banco de dados.
- Retorna códigos de status apropriados para sucesso (204) e erros (400, 500).

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot**
  - Spring Web
  - Spring Data JPA
- **MySQL** (com suporte a H2 em memória para testes)
- **Lombok**
- **OpenAPI** (Swagger)
- **Docker Compose** (para configuração do banco de dados)

## Como Executar

### Pré-requisitos

- Java 17+
- Docker e Docker Compose
- Maven

### Configuração do Banco de Dados

1. Suba o container do MySQL com o comando:
   ```bash
   docker-compose up -d
   ```

2. Certifique-se de que o banco de dados está rodando corretamente na porta `3307`.

### Execução da Aplicação

1. Clone o repositório:
   ```bash
   git clone git@github.com:luanbrazz/viassoft-backend-challenge.git
   cd email-service
   ```

2. Compile e inicie o projeto:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. Acesse a documentação Swagger da API:
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

## Endpoints

### **Enviar e-mail**

- **POST** `/api/emails`
  - Envia um e-mail adaptado para AWS ou OCI.
  - Corpo da requisição:
    ```json
    {
      "recipient": "luanbraz2019@gmail.com",
      "recipientName": "Luan Braz",
      "sender": "luan_silvacpv@hotmail.com",
      "subject": "Bem-vindo ao nosso serviço!",
      "content": "Olá, obrigado por se juntar ao nosso serviço!"
    }
    ```

- **Configuração da integração**:
  Configure o arquivo `application.properties` para usar `AWS` ou `OCI`:
  ```properties
  mail.integracao=AWS
  ```

### **Auditar e-mails**

- **GET** `/api/emails/audit`
  - Consulta logs de auditoria paginados, filtrando pelo status (`SUCCESS` ou `FAILURE`).
  - Exemplo de requisição:
    ```
    GET /api/emails/audit?status=SUCCESS&page=0&size=20
    ```

## Estrutura do Projeto

- **Controller**: Camada responsável por receber as requisições REST.
- **Service**: Camada de lógica de negócios.
- **Repository**: Interface para acesso ao banco de dados.
- **DTO**: Objetos para transferência de dados entre as camadas.
- **Entity**: Representação das tabelas no banco de dados.
- **Exception Handling**: Tratamento de erros customizados para a API.

## Testes

- **Testes unitários**: Podem ser implementados para validação das funcionalidades do serviço.
- **Banco de dados em memória**: Durante os testes, a aplicação utiliza H2 para simular o banco de dados.

## Melhorias Futuras

- Implementação de autenticação e autorização.
- Testes unitários e de integração mais robustos.
- Deployment em ambiente cloud.

## Licença

Projeto desenvolvido exclusivamente para o desafio técnico da Viasoft.

--- 