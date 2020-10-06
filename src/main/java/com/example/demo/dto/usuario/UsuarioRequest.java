package com.example.demo.dto.usuario;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Pattern;

@Data // Implementa os get e set, alem de equals, tostring e hashcode, em tempo de compilação
@Builder // Cria um UsuarioRequest.builder() pra que eu possa instanciar um objeto
public class UsuarioRequest {

    private Long id;

    @NonNull // Usa o spring validations para retornar um BadRequest caso o parametro enviado seja nulo
    private String nome;

    @Pattern(regexp = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$") // Usa o spring validations para validar se o conteudo da mensagem veio no padrão esperado ou retorna bad request
    private String email;

}
