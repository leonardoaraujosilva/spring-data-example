package com.example.demo.service;

import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.repository.UsuarioRepository;
import com.example.demo.dto.usuario.UsuarioFilter;
import com.example.demo.dto.usuario.UsuarioRequest;
import com.example.demo.dto.usuario.UsuarioResponse;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.usuario.UsuarioMapper;
import com.example.demo.mapper.usuario.UsuarioResponseMapper;
import com.example.demo.rest.UsuarioApiService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service // Avisa ao spring que esse também é um bean que deverá ser gerenciado
public class UsuarioService implements UsuarioApiService {

    // Um bean que será injetado via construtor pelo spring que faz o gerenciamento de banco por extender de JpaRepository
    // Ele não precisa de nenhuma implementação
    private final UsuarioRepository usuarioRepository;

    // Receberá no construtor um objeto singleton instanciado pelo spring do tipo usuarioMapper
    private final UsuarioMapper usuarioMapper;;

    // Receberá no construtor um objeto singleton instanciado pelo spring do tipo usuarioResponseMapper
    private final UsuarioResponseMapper usuarioResponseMapper;

    // Injeta o bean do banco para a classe de serviço usar
    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, UsuarioResponseMapper usuarioResponseMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.usuarioResponseMapper = usuarioResponseMapper;
    }


    @Override
    public UsuarioResponse saveOrUpdate(UsuarioRequest usuarioRequest) {

        // Faz o mapeamento do request para a entidade
        var usuario = usuarioMapper.fromUsuarioRequest(usuarioRequest);

        // Verifica se já existe
        if(usuarioRequest.getId() != null) {
            var usuarioAnterior = usuarioRepository.findById(usuarioRequest.getId()).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
            usuario.setDataHoraCriacao(usuarioAnterior.getDataHoraCriacao());
        }

        // Salva imediatamente o usuario no banco
        // Se for enviado um ID, ele fará um update, sem Id ele irá fazer um insert
        //
        // Poderia ser usado apenas save(usuario) nesse caso, que deixaria o entitymanager (gerenciador do banco)
        // criar uma execução em batch no banco, executando da maneira mais otimizada possivel
        // Em alguns casos pode ser que seja do seu interesse usar o flush e forçar a execução
        // Tanto saveAndFlush quanto save retornam o mesmo objeto que o enviado no parâmetro, fica a seu critério como usar
        usuarioRepository.saveAndFlush(usuario);

        // Mapeia a entidade para um objeto de resposta
        return usuarioResponseMapper.fromUsuario(usuario);
    }

    @Override
    public UsuarioResponse findById(Long id) {
        // Procura um usuario por id no banco, lança runtimeexception se não encontrado
        // Pode ser criada uma exceção customizada e criar retornos customizados usando classes de configuração @ExceptionHandler
        // não precisando ser feito tratamento nas camadas superiores, o proprio spring tratará as @ExceptionHandler configuradas
        var usuario = usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        // Mapeia a entidade para um objeto de resposta
        return usuarioResponseMapper.fromUsuario(usuario);
    }

    @Override
    public List<UsuarioResponse> findAll(UsuarioFilter usuarioFilter, Pageable pageable) {


        // Deve ser feita a configuração do funcionamento do filtro, baseado nos valores preenchidos no objeto Example.of criado abaixo
        var exampleMatcher = ExampleMatcher.matchingAny() // Significa que deve usar uma OR para filtrar
            .withIgnoreNullValues() // Os campos null não serão utilizados para filtragem, sem isso ele irá comparar se os valores dos campos são null
            .withIgnoreCase(); // Ignora diferenças entre maiusculas e minusculas

        // Example.of faz parte do ExampleMatcher do spring data, que tem algumas opções de filtro nativas
        // Filtragens mais complexas depende de queries criadas manualmente, seja usando @Query ou queryByName
        // Para os dois casos haverão exemplos na UsuarioRepository
        var filtro = Example.of(usuarioMapper.fromUsuarioFilter(usuarioFilter), exampleMatcher);

        // Executa a busca filtrada no banco
        var usuarioList = usuarioRepository.findAll(filtro, pageable);

        // Agora usaremos a biblioteca de stream do java 8 para mapear a lista de usuarios para uma lista de usuarioResponse
        return usuarioList.stream()
                .map(usuario -> usuarioResponseMapper.fromUsuario(usuario))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    // Anotação que diz para executar uma transação
    // Quando em uma transação, as alterações que voce fizer em um buscado do banco objeto após um findById, por exemplo
    // Serão commitadas no fim da execução do método
    // Transactional também diz que caso algum erro ocorra no meio da operação, tudo que foi feito sofrerá rollback
    // Se o método for concluído lançar sem exceção, as operações são commitadas
    public void deleteById(Long id) {

        // Para esse caso será feita apenas uma deleção logica para exemplificação
        // caso queira fazer uma deleção fisica apenas chamar usuarioRepository.deleteById(id)
        var usuario = usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }
}
