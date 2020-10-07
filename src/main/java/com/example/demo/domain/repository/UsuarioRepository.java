package com.example.demo.domain.repository;

import com.example.demo.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// Faz o CRUD completo e mais algumas opções de filtragem, apenas por extender de jparepository
// O primeiro parametro de jparepository é o tipo da classe de entidade, o segundo o tipo do Id dessa classe
// Nenhum codigo precisa ser implementado, é isso que faz o spring data
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Criando uma query JPQL para exemplificar, atenção que aqui valem os nomes das classes e objetos java
    // Sendo possivel retornar DTOs usando new e o nome completo do DTO, inclusive pacote
    // Se referencia as variaveis na query usando :nome_variavel, os campos também são referentes as variaveis java
    // Não as colunas do banco
    // Para ver mais procure por JPQL
    @Query("select u from Usuario u where u.id=:id and u.nome=:nome order by u.nome desc")
    List<Usuario> findAllByFilters(Long id, String nome);

    // Outra opção é fazer a mesma coisa usando query by name
    // Apenas pelo nome do método voce consegue fazer a mesma configuração
    // Usando os nomes dos campos e as palavras chave, o spring saberá criar a query
    // A consulta abaixo fará a mesma coisa que a de cima, só foram feitas de formas diferentes
    // Procure por querybyname spring data para ver mais
    List<Usuario> findAllByIdAndNomeOrderByNomeDesc(Long id, String nome);
}
