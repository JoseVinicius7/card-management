# Microserviço de Gerenciamento de Cartões
**Descrição**: Microserviço completo para gestão de cartões com operações de criação, ativação e cancelamento. Desenvolvido com Java 21, Spring Boot 3.2.4, Kafka e PostgreSQL. Documentação via SpringDoc OpenAPI 2.3.0.

## 📋Tecnologias:
Java 21
* Spring Boot 3.2.4 (Web/Data JPA/Validation)
*  MapStruct 1.5.5
* Lombok 1.18.30
* PostgreSQL 42.5.4
* Kafka
* SpringDoc OpenAPI 2.3.0
* JUnit 5
* Mockito

## 🔧 Configuração e Instalação
* JDK 21 + Kafka + PostgreSQL
* `git clone https://github.com/JoseVinicius7/card-management.git`
* Configure `application.yaml`:
```properties  
spring.datasource.url=jdbc:postgresql://localhost:5432/card_db  
spring.datasource.username=postgres  
spring.datasource.password=senha  
spring.kafka.bootstrap-servers=localhost:9092  
server.port=8080  
```
## 🚀 Funcionalidades

- Criação de cartões
- Ativação de cartões
- Cancelamento de cartões
- Documentação da API com Swagger
- Integração com mensageria para eventos de cartão

## 📦 Pré-requisitos

- Java JDK 21
- Maven
- Kafka
- Banco de dados (PostgreSQL)

##  Execute o projeto:

`mvn spring-boot:run`

## 📚 Documentação da API

A documentação da API está disponível através do Swagger UI:

http://localhost:8080/swagger-ui.html

**Título**: API de Gestão de Cartões

**Contato**: José Vinicius Alves dos Santos (jose.vinicius7@hotmail.com)
### Endpoints Principais

#### Criar Cartões
```
http
POST /api/v1/cartoes
```
#### Ativar Cartão
```
http
PATCH /api/v1/cartoes/{id}/ativar
```
#### Cancelar Cartão
```
http
PATCH /api/v1/cartoes/{id}/cancelar
```
## 🧪 Testes

O projeto inclui testes unitários utilizando JUnit 5 e Mockito. Para executar os testes:

`mvn test`

### Cobertura de Testes

Os testes incluem verificações para:
- Criação de cartões
- Ativação de cartões
- Cancelamento de cartões
- Cenários de erro e exceções

## 📊 Eventos e Mensageria

O serviço utiliza Kafka para publicar eventos nos seguintes cenários:
- Cartão criado
- Cartão ativado
- Cartão cancelado

## 🏗️ Arquitetura
```

src/
├── main/
│   ├── java/
│   │   └── com/ms/cardmanagement/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── mapper/
│   │       ├── messaging/
│   │       ├── model/
│   │       ├── repository/
│   │       └── service/
│   └── resources/
└── test/
└── java/
```
## 👥 Guia de Contribuição

Para contribuir com este projeto, siga este fluxo:

1. **Faça um fork** do repositório
2. **Crie uma branch** para sua feature/correção:
   ```
   git checkout -b feat/nome-da-feature   # para novas funcionalidades
   git checkout -b fix/nome-da-correcao   # para correções de bugs
   ```
3. Faça commits atômicos com mensagens claras:
   ```
   git commit -m "feat: adiciona validação de cartão"
   git commit -m "fix: corrige NPE na ativação"
   ```
4. Envie as alterações para seu repositório:
   ```
    git push origin feat/nome-da-feature
   ```
5. Abra um Pull Request no repositório original com:
   ```
    * Descrição clara das mudanças
    * Motivação para a alteração
    * Screenshots (se aplicável)
    ```
## 📝 Licença

Este projeto está sob a licença MIT - veja o arquivo [LICENSE.md](LICENSE.md) para mais detalhes.

## 📞 Suporte

Para suporte, envie um email para [jose.vinicius7@hotmail.com] ou abra uma issue no repositório.

## 📌 Visão Geral do README

Este documento abrange todos os aspectos essenciais do projeto:

**Conteúdo Principal**:
- 🎯 Descrição do propósito e funcionalidades do microserviço
- 🛠️ Stack tecnológica completa com versões específicas
- 🚀 Guia passo a passo de instalação e configuração
- 📚 Documentação da API com exemplos de endpoints
- 🧪 Estratégia de testes e cobertura
- 🏗️ Diagrama da arquitetura e estrutura de diretórios
- 👥 Processo de contribuição detalhado