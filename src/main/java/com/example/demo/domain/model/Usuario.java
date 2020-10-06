package com.example.demo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity // Indica para o java que essa classe é de entidade de banco de dados, é usada em JPQL quando necessário (ver repository que terá um exemplo)
@Table(name = "usuario") // Caso não seja anotada com @Table e passado um name, a tabela de banco criada será o nome da classe. Essa anotação não é obrigatória
@Data // Anotação do lombok para GET, SET, Equals, HashCode e ToString
@Builder // Anotação do lombok para criar um metodo do padrão builder para essa classe
@NoArgsConstructor // Cria um construtor vazio usando lombok
@AllArgsConstructor // Cria um construtor com todos os parametros usando lombok
public class Usuario {

    @Id // Indica para o java que esse campo é o Id da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indica para o banco que o método de geração será usado tipo identity do banco
    // Nem todos bancos suportam esse tipo de geração, podendo usar sequences também, como no oracle ou uma tabela de controle auto gerenciada
    private Long id;

    @Column(name = "nome_usuario", nullable = false) // Indica que é uma coluna e permite algumas configurações
    // se o name não for enviado, ele usará o nome da variavel
    private String nome;

    @Column // Cria uma coluna nullable no banco com nome email
    private String email;

    @Column(nullable = false, updatable = false) // Define que a coluna é not null e não pode ser atualizada
    @Builder.Default // Avisa ao lombok que caso esse campo não seja enviado para o builder, usar esse valor default da propriedade, do contrário ficaria null
    private LocalDateTime dataHoraCriacao = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

}
