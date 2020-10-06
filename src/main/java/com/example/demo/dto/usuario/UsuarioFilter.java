package com.example.demo.dto.usuario;

import lombok.Data;

@Data
public class UsuarioFilter {

    private Long id;
    private String nome;
    private String email;
    private Boolean ativo = true;

}
