package com.example.demo.rest;

import com.example.demo.dto.usuario.UsuarioFilter;
import com.example.demo.dto.usuario.UsuarioRequest;
import com.example.demo.dto.usuario.UsuarioResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

// Essa interface não é necessária, mas recomendada para fazer a configuração do endpoint
public interface UsuarioApi {

    // @PostMapping configura um endpoint POST. Quando a url for chamada com post, ele irá executar esse método
    // @RequestBody faz com que seja obrigatório voce enviar um objeto (no caso um json) que preencha o objeto UsuarioRequest
    // @Valid significa que ele vai validar o objeto enviado com as anotações que voce fizer na classe UsuarioRequest
    // Um ResponseEntity é um objeto que possibilita configurar com detalhes um retorno HTTP
    // UsuarioRequest e UsuarioResponse são os meus DTOs de input e output pra esse método
    @PostMapping(value = "/api/v1/usuario", consumes = "application/json", produces = "application/json")
    ResponseEntity<UsuarioResponse> saveOrUpdate(@Valid @RequestBody UsuarioRequest usuarioRequest);

    // @GetMapping Cria um GET para a url em questão, recebe um Objeto do tipo usuarioFilter que deve ser preenchido via queryParam
    // Por exemplo /api/v1/usuario?nome=exemplo&id=3
    // @PageableDefault cria um valor default para o pageable, caso os queryParam sort, size e page não sejam enviados
    @GetMapping(value = "/api/v1/usuario", produces = "application/json")
    ResponseEntity<List<UsuarioResponse>> findAll(UsuarioFilter usuarioFilter, @PageableDefault(page = 0, size = 10, sort = { "nome", "id" }, direction = Sort.Direction.ASC) Pageable pageable);

    // @GetMapping Cria um GET para a url, recebendo uma variabel que será mapeada para o @PathVariable que tiver o mesmo nome
    // exemplo /api/v1/usuario/32, o valor de usuarioId será 32, é permitido criar urls com mais variaveis e mais coisas após as variavel
    // A ? dentro do responseEntity indica que permite qualquer tipo de objeto, ela será necessária pra quando não encontrarmos nenhum usuario com o Id, retornando um 404 vazio
    @GetMapping(value = "/api/v1/usuario/{usuarioId}", produces = "application/json")
    ResponseEntity<?> findByid(@PathVariable Long usuarioId);

    // @DeleteMapping Cria um DELETE para a url, recebendo uma variabel que será mapeada para o @PathVariable que tiver o mesmo nome
    // exemplo /api/v1/usuario/32, o valor de usuarioId será 32, é permitido criar urls com mais variaveis e mais coisas após as variavel
    // A ? dentro do responseEntity indica que permite qualquer tipo de objeto,
    // ela será necessária pra quando não encontrarmos nenhum usuario com o Id, retornando um 404 vazio
    // Métodos delete também podem retornar 204 (NO_CONTENT) em caso de sucesso, sem retornar nenhuma informação no corpo da mensagem
    // Metodos DELETE devem ser utilizados para exclusão logica ou fisica da entidade
    @DeleteMapping(value = "/api/v1/usuario/{usuarioId}")
    ResponseEntity<?> deleteById(@PathVariable Long usuarioId);
}
