package com.codigo.Gestion.Usuarios.service;

import com.codigo.Gestion.Usuarios.aggregates.request.SignUpRequest;
import com.codigo.Gestion.Usuarios.aggregates.response.BaseResponse;
import com.codigo.Gestion.Usuarios.entity.UsuarioEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;


public interface UsuarioService {
    UserDetailsService userDetailsService();
    BaseResponse getUserByDni(String dni);
    List<UsuarioEntity> getAllUsers();
    BaseResponse updateUser(Long id, SignUpRequest signUpRequest);
    BaseResponse deleteUser(Long id);


}
