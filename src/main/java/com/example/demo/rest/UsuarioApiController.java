package com.example.demo.rest;

import com.example.demo.dto.usuario.UsuarioFilter;
import com.example.demo.dto.usuario.UsuarioRequest;
import com.example.demo.dto.usuario.UsuarioResponse;
import com.example.demo.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController // Indica para o spring que é uma controller, para criar o bean
public class UsuarioApiController implements UsuarioApi {

    // Bean instanciado pelo spring. Usamos interface para facilitar um evolução futura do componente
    // Podendo que eu crie diferentes implementações que devem ser usadas em momentos diferentes
    // Eu posso injetar esse bean usando o construtor, como feito nessa ocasiao,
    // ou apenas removendo o final e anotando com @Autowired.
    // Isso chama injeção de dependências, é um conceito comum em grandes sistemas
    private final UsuarioApiService usuarioApiService;

    // Adiciono os beans que eu preciso, nesse caso, apenas UsuarioService
    // O spring irá procurar pelas classes desse tipo anotadas como @Service, @Component, @Repository (se não me engano
    // também por @Controller e @RestController), instanciar um objeto singleton delas, e me fornecer esse objeto nesse contrutor
    // Eu não usarei new em nenhum lugar
    public UsuarioApiController(UsuarioApiService usuarioApiService) {
        this.usuarioApiService = usuarioApiService;
    }

    @Override
    public ResponseEntity<UsuarioResponse> saveOrUpdate(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        // Monta a resposta http, no caso se for enviado ID, ele irá atualizar (retornando um 200, que significa OK)
        // Mas se for enviado sem ID, ele irá criar um novo objeto e retornar um CREATED, status 201
        return ResponseEntity.status(usuarioRequest.getId() == null ? HttpStatus.CREATED : HttpStatus.OK)
                .body(usuarioApiService.saveOrUpdate(usuarioRequest));
    }

    @Override
    public ResponseEntity<List<UsuarioResponse>> findAll(UsuarioFilter usuarioFilter, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(usuarioApiService.findAll(usuarioFilter, pageable));
    }

    @Override
    public ResponseEntity<?> findByid(Long usuarioId) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(usuarioApiService.findById(usuarioId));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity.notFound()
                    .build();
        }
    }

    @Override
    public ResponseEntity<?> deleteById(Long usuarioId) {
        try {
            usuarioApiService.deleteById(usuarioId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .build();
        } catch (NotFoundException notFoundException) {
            return ResponseEntity.notFound()
                    .build();
        }
    }

}
