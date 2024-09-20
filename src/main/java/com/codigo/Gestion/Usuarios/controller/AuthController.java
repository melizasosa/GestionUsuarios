package com.codigo.Gestion.Usuarios.controller;


import com.codigo.Gestion.Usuarios.aggregates.request.SignInRequest;
import com.codigo.Gestion.Usuarios.aggregates.request.SignUpRequest;
import com.codigo.Gestion.Usuarios.aggregates.response.BaseResponse;
import com.codigo.Gestion.Usuarios.aggregates.response.SignInResponse;
import com.codigo.Gestion.Usuarios.entity.UsuarioEntity;
import com.codigo.Gestion.Usuarios.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la autenticación de usuarios.
 */
@RestController
@RequestMapping("api/v1/authentication/")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;


    /**
     * Registra un nuevo usuario con rol estándar.
     *
     * @param signUpRequest Datos del usuario a registrar.
     * @return Respuesta con la entidad del usuario registrado.
     */
    @PostMapping("/signupuser")
    public ResponseEntity<BaseResponse> signUpUser(@RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authenticationService.signUpUser(signUpRequest));
    }

    /**
     * Registra un nuevo usuario con rol de administrador.
     *
     * @param signUpRequest Datos del usuario a registrar.
     * @return Respuesta con la entidad del administrador registrado.
     */
    @PostMapping("/signupadmin")
    public ResponseEntity<BaseResponse> signUpAdmin(@RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authenticationService.signUpAdmin(signUpRequest));
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signin(@RequestBody SignInRequest signInRequest){
        return ResponseEntity.ok(authenticationService.signIn(signInRequest));
    }

}
