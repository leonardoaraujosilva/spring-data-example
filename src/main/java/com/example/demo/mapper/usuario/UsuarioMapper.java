package com.example.demo.mapper.usuario;

import com.example.demo.domain.model.Usuario;
import com.example.demo.dto.usuario.UsuarioFilter;
import com.example.demo.dto.usuario.UsuarioRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component // Diz para o spring que esse é um bean gerenciado, podendo ser injetado via construtor para outros beans
public class UsuarioMapper {

    public Usuario fromUsuarioRequest(UsuarioRequest usuarioRequest) {
        return Usuario.builder() // Usa o @Buider do lombok para criar um objeto preenchido
                .id(usuarioRequest.getId())
                .nome(usuarioRequest.getNome())
                .email(usuarioRequest.getEmail())
                .dataHoraCriacao(LocalDateTime.now()) // Não é necessário pois o @Builder.Default já indica que será esse o valor
                .ativo(true)
                .build();
    }

    public Usuario fromUsuarioFilter(UsuarioFilter usuarioFilter) {
        return Usuario.builder()
                .id(usuarioFilter.getId())
                .nome(usuarioFilter.getNome())
                .email(usuarioFilter.getEmail())
                .ativo(usuarioFilter.getAtivo())
                .build();
    }

}
