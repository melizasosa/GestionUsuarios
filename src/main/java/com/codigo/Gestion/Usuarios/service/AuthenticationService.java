package com.codigo.Gestion.Usuarios.service;

import com.codigo.Gestion.Usuarios.aggregates.request.SignInRequest;
import com.codigo.Gestion.Usuarios.aggregates.request.SignUpRequest;
import com.codigo.Gestion.Usuarios.aggregates.response.SignInResponse;
import com.codigo.Gestion.Usuarios.entity.UsuarioEntity;



public interface AuthenticationService {
    //SIGNUP -> INSCRIBIRSE
    UsuarioEntity signUpUser(SignUpRequest signUpRequest);
    UsuarioEntity signUpAdmin(SignUpRequest signUpRequest);

}
