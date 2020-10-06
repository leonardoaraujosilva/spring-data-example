package com.example.demo.mapper.usuario;

import com.example.demo.domain.model.Usuario;
import com.example.demo.dto.usuario.UsuarioResponse;
import org.springframework.stereotype.Component;

@Component // Diz para o spring que esse é um bean gerenciado, podendo ser injetado via construtor para outros beans
public class UsuarioResponseMapper {

    public UsuarioResponse fromUsuario(Usuario usuario) {
        return UsuarioResponse.builder() // Padrão builder criado pelo @Builder adicionado na classe UsuarioResponse
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .dataHoraCriacao(usuario.getDataHoraCriacao())
                .ativo(usuario.isAtivo())
                .build();
    }

}
