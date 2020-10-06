package com.example.demo.dto.usuario;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private LocalDateTime dataHoraCriacao;
    private boolean ativo;

}
