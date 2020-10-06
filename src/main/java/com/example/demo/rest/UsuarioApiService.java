package com.example.demo.rest;

import com.example.demo.dto.usuario.UsuarioFilter;
import com.example.demo.dto.usuario.UsuarioRequest;
import com.example.demo.dto.usuario.UsuarioResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsuarioApiService {

    UsuarioResponse saveOrUpdate(UsuarioRequest usuarioRequest);

    UsuarioResponse findById(Long id);

    // O pageable é do spring e permite paginação opcional
    // Enviando na url, por exemplo /api/v1/usuario?page=0&size=5&sort=nome,DESC
    // voce terá a primeira pagina, com 5 resultados em ordem descendente pelo campo nome
    // Os tres parametros podem ser enviados separadamente, ou não enviados
    List<UsuarioResponse> findAll(UsuarioFilter usuarioFilter, Pageable pageable);

    void deleteById(Long usuarioId);

}
