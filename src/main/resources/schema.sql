DROP TABLE IF EXISTS cartao;

CREATE TABLE cartao
(
    id               SERIAL PRIMARY KEY,
    cpf              VARCHAR(14)  NOT NULL,
    nome_impresso    VARCHAR(100) NOT NULL,
    produto          VARCHAR(50)  NOT NULL,
    subproduto       VARCHAR(50)  NOT NULL,
    tipo_cartao      VARCHAR(20)  NOT NULL,
    situacao         VARCHAR(20)  NOT NULL,
    data_criacao     TIMESTAMP    NOT NULL,
    data_atualizacao TIMESTAMP,
    data_ativacao    TIMESTAMP
);
