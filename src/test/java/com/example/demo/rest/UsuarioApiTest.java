package com.example.demo.rest;

import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.repository.UsuarioRepository;
import com.example.demo.dto.usuario.UsuarioRequest;
import com.example.demo.dto.usuario.UsuarioResponse;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j // Anotação para logs do lombok, pode ser usada em qualquer classe, inclusive não testes, como log.info, log.error, log.warn e log.debug
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
// Indica que esse é um teste de integração. O sistema sobe um servidor e executa os testes descritos aqui
public class UsuarioApiTest {

    private static final String EMAIL_USUARIO = "joao_da_silva@email.com";
    private static final String EMAIL_USUARIO_INVALIDO = "nao_sou_um_email_valido.com";
    private static final String NOME_USUARIO = "João da Silva";
    private static final String NOVO_NOME_USUARIO = "Silva da João";
    private static final String NOME_USUARIO_OP_2 = "Qualquer nome";
    private static final String NOME_USUARIO_OP_3 = "Qualquer nome mais um";

    private static final String API_BASE = "/api/v1/usuario";
    private static final String API_BUSCA_ID = "/api/v1/usuario/%d";

    private static final Long ID_NAO_ESPERADO = 1000000L;

    @LocalServerPort // Informa para a classe em qual porta o servidor de testes subiu
    private int port;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach // método que é chamado antes de cada teste
    public void beforeEach() {
        RestAssured.port = port; // Configura o rest-assured para usar a porta em que o servidor de testes subiu
    }

    @AfterEach // método que é executado depois de cada teste
    public void afterEach() {
        // Limpa o banco após o teste
        usuarioRepository.deleteAll();
    }

    @Test // Um teste que será executado
    public void deveCriarUmNovoUsuarioApenasComNomeComSucesso() {

        var response = criarUsuarioComNome();

        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode()); // Verifica se o status retornado pelo servidor é 201

        var responseBody = response.as(UsuarioResponse.class); // Mapeia o corpo da resposta da api de um json para a classe UsuarioResponse

        Assertions.assertEquals(NOME_USUARIO, responseBody.getNome()); // Compara se o nome retornado é o mesmo enviado
        Assertions.assertNull(responseBody.getEmail()); // Verifica se o email é nulo (pois não foi enviado)
        Assertions.assertNotNull(responseBody.getId()); // Verifica se o id foi gerado (pois o usuário deve ter sido criado)
        Assertions.assertNotNull(responseBody.getDataHoraCriacao()); // Verifica se a data e hora de criação trazida na resposta foi gerada
        Assertions.assertTrue(responseBody.isAtivo()); // Verifica se usuario foi criado como ativo

        // Busca o usuário no banco para comparar com a resposta recebida
        var usuario = usuarioRepository.findById(responseBody.getId()).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Assertions.assertEquals(usuario.getId(), responseBody.getId());
        Assertions.assertEquals(usuario.getDataHoraCriacao().truncatedTo(ChronoUnit.MILLIS), responseBody.getDataHoraCriacao().truncatedTo(ChronoUnit.MILLIS));
        Assertions.assertEquals(usuario.getEmail(), responseBody.getEmail());
        Assertions.assertEquals(usuario.getNome(), responseBody.getNome());
        Assertions.assertEquals(usuario.isAtivo(), responseBody.isAtivo());
    }

    @Test
    public void deveCriarUsuarioApenasComNomeEAtualizarUsuarioComEmailValido() {

        var response = criarUsuarioComNome();
        var responseBody = response.as(UsuarioResponse.class);
        var generatedId = responseBody.getId();
        var generatedTimestamp = responseBody.getDataHoraCriacao();

        var request = UsuarioRequest.builder()
                .id(responseBody.getId())
                .nome(NOVO_NOME_USUARIO)
                .email(EMAIL_USUARIO)
                .build();

        // O objeto acima é equivalente ao seguinte json: { "nome": "Silva da João", "email": "joao_da_silva@email.com" }

        response = RestAssured.given() // Coisas que serão enviadas vem depois do given
                .body(request) // transforma o objeto da request em um json equivalente que será enviado
                .contentType(ContentType.APPLICATION_JSON.getMimeType()) // Avisa que está enviando um json
                .post(API_BASE); // Executa o post na url informada, enviando o objeto de given.body como json

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());

        responseBody = response.as(UsuarioResponse.class);
        Assertions.assertEquals(generatedId, responseBody.getId()); // Não deve ter sido alterado
        Assertions.assertEquals(generatedTimestamp.truncatedTo(ChronoUnit.MILLIS), responseBody.getDataHoraCriacao().truncatedTo(ChronoUnit.MILLIS)); // Não deve ter sido alterado
        Assertions.assertEquals(NOVO_NOME_USUARIO, responseBody.getNome()); // Compara se o nome retornado é o mesmo, ou seja, atualizou os dados
        Assertions.assertEquals(EMAIL_USUARIO, responseBody.getEmail()); // Verifica se o email é foi registrado
    }

    @Test
    public void deveFalharAoTentarEnviarEmailInvalido() {
        var request = UsuarioRequest.builder()
                .nome(NOME_USUARIO)
                .email(EMAIL_USUARIO_INVALIDO)
                .build();

        // O objeto acima é equivalente ao seguinte json: { "nome": "João da Silva", "email": "nao_sou_um_email_valido.com" }
        // Enviando esse json para a url que chamaremos abaixo, terá o mesmo efeito

        var response = RestAssured.given() // Coisas que serão enviadas vem depois do given
                .body(request) // transforma o objeto da request em um json equivalente que será enviado
                .contentType(ContentType.APPLICATION_JSON.getMimeType()) // Avisa que está enviando um json
                .post(API_BASE); // Executa o post na url informada, enviando o objeto de given.body como json

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
    }

    @Test
    public void deveBuscarPorId() {

        var usuario1 = criaUsuarioNoBanco(NOME_USUARIO, null);
        var usuario2 = criaUsuarioNoBanco(NOVO_NOME_USUARIO, null);
        var usuario3 = criaUsuarioNoBanco(NOME_USUARIO_OP_2, null);
        var usuario4 = criaUsuarioNoBanco(NOME_USUARIO_OP_3, null);

        var response = RestAssured.
                get(String.format(API_BUSCA_ID, usuario1)); // Executa um get na url informada por parametro
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode()); // Valida se encontrou
        Assertions.assertEquals(NOME_USUARIO, response.as(UsuarioResponse.class).getNome()); // Valida se o nome é o esperado

        response = RestAssured.
                get(String.format(API_BUSCA_ID, usuario2));
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode()); // Valida se encontrou
        Assertions.assertEquals(NOVO_NOME_USUARIO, response.as(UsuarioResponse.class).getNome()); // Valida se o nome é o esperado

        response = RestAssured.
                get(String.format(API_BUSCA_ID, usuario3));
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode()); // Valida se encontrou
        Assertions.assertEquals(NOME_USUARIO_OP_2, response.as(UsuarioResponse.class).getNome()); // Valida se o nome é o esperado

        response = RestAssured.
                get(String.format(API_BUSCA_ID, usuario4));
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode()); // Valida se encontrou
        Assertions.assertEquals(NOME_USUARIO_OP_3, response.as(UsuarioResponse.class).getNome()); // Valida se o nome é o esperado
    }

    @Test
    public void deveNaoEncontrarAoBuscarPorId() {
        var response = RestAssured
                .get(String.format(API_BUSCA_ID, ID_NAO_ESPERADO));
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
    }

    @Test
    public void deveBuscarTodosPaginado() {

        var usuario1 = criaUsuarioNoBanco(NOME_USUARIO, null);
        var usuario2 = criaUsuarioNoBanco(NOVO_NOME_USUARIO, null);
        var usuario3 = criaUsuarioNoBanco(NOME_USUARIO_OP_2, null);
        var usuario4 = criaUsuarioNoBanco(NOME_USUARIO_OP_3, null);

        int page = 1;
        int size = 2;

        var response = RestAssured
                .get(String.format("%s?page=%d&size=%d&sort=id", API_BASE, page, size));

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());

        var responseBody = response.as(UsuarioResponse[].class);

        Assertions.assertEquals(size, responseBody.length);
        Assertions.assertEquals(usuario3, responseBody[0].getId());
        Assertions.assertEquals(usuario4, responseBody[1].getId());
    }

    @Test
    public void deveRealizarExclusaoLogicaNoUsuario() {

        var usuario1 = criaUsuarioNoBanco(NOME_USUARIO, null);

        // busca no banco pra garantir que existe um
        var response = RestAssured
                .get(API_BASE);
        Assertions.assertEquals(1, response.as(List.class).size());

        response = RestAssured.
                delete(String.format(API_BUSCA_ID, usuario1)); // Executa um get na url informada por parametro
        Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode());

        // Valida que não existe mais nenhum (default de buscas é ativo true)
        response = RestAssured
                .get(API_BASE);
        Assertions.assertEquals(0, response.as(List.class).size());

        var usuario = usuarioRepository.findById(usuario1).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Assertions.assertFalse(usuario.isAtivo());

        // Deve continuar trazendo em busca por id
        var responseBody = RestAssured
                .get(String.format(API_BUSCA_ID, usuario1))
                .as(UsuarioResponse.class);
        Assertions.assertFalse(responseBody.isAtivo());
    }


    private Response criarUsuarioComNome()
    {
        var request = UsuarioRequest.builder()
                .nome(NOME_USUARIO)
                .build();

        // O objeto acima é equivalente ao seguinte json: { "nome": "João da Silva" }
        // Enviando esse json para a url que chamaremos abaixo, terá o mesmo efeito

        var response = RestAssured.given() // Coisas que serão enviadas vem depois do given
                .body(request) // transforma o objeto da request em um json equivalente que será enviado
                .contentType(ContentType.APPLICATION_JSON.getMimeType()) // Avisa que está enviando um json
                .post(API_BASE); // Executa o post na url informada, enviando o objeto de given.body como json

        log.info("Json response: {}", response.getBody().asString());

        return response;
    }

    private Long criaUsuarioNoBanco(String nome, String email) {
        var usuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .dataHoraCriacao(LocalDateTime.now())
                .build();

        usuarioRepository.saveAndFlush(usuario);
        return usuario.getId();
    }
}
