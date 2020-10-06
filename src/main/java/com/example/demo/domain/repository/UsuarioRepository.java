package com.example.demo.domain.repository;

import com.example.demo.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

// Faz o CRUD completo e mais algumas opções de filtragem, apenas por extender de jparepository
// O primeiro parametro de jparepository é o tipo da classe de entidade, o segundo o tipo do Id dessa classe
// Nenhum codigo precisa ser implementado, é isso que faz o spring data
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
