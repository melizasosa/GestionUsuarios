package com.codigo.Gestion.Usuarios.service;

import com.codigo.Gestion.Usuarios.aggregates.request.SignInRequest;
import com.codigo.Gestion.Usuarios.aggregates.request.SignUpRequest;
import com.codigo.Gestion.Usuarios.aggregates.response.BaseResponse;
import com.codigo.Gestion.Usuarios.aggregates.response.SignInResponse;
import com.codigo.Gestion.Usuarios.entity.UsuarioEntity;

import java.util.List;


public interface AuthenticationService {
    BaseResponse signUpUser(SignUpRequest signUpRequest);
    BaseResponse signUpAdmin(SignUpRequest signUpRequest);
    SignInResponse signIn(SignInRequest signInRequest);

}
