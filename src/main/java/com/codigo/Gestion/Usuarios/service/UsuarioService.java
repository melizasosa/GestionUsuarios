package com.codigo.Gestion.Usuarios.service;



import com.codigo.Gestion.Usuarios.aggregates.request.SignUpRequest;
import com.codigo.Gestion.Usuarios.aggregates.response.BaseResponse;
import com.codigo.Gestion.Usuarios.entity.UsuarioEntity;

import java.util.List;
import java.util.Optional;


public interface UsuarioService {

    UsuarioEntity getUserByDni(String dni);
    List<UsuarioEntity> getAllUsers();
    UsuarioEntity updateUser(Long id, SignUpRequest signUpRequest);

    void deleteUser(Long id);


}
