package com.codigo.Gestion.Usuarios.service.impl;

import com.codigo.Gestion.Usuarios.aggregates.constants.Constants;
import com.codigo.Gestion.Usuarios.aggregates.request.SignInRequest;
import com.codigo.Gestion.Usuarios.aggregates.request.SignUpRequest;
import com.codigo.Gestion.Usuarios.aggregates.response.BaseResponse;
import com.codigo.Gestion.Usuarios.aggregates.response.ReniecResponse;
import com.codigo.Gestion.Usuarios.aggregates.response.SignInResponse;
import com.codigo.Gestion.Usuarios.client.ReniecClient;
import com.codigo.Gestion.Usuarios.entity.RolEntity;
import com.codigo.Gestion.Usuarios.entity.Role;
import com.codigo.Gestion.Usuarios.entity.UsuarioEntity;
import com.codigo.Gestion.Usuarios.repository.RolRepository;
import com.codigo.Gestion.Usuarios.repository.UsuarioRepository;
import com.codigo.Gestion.Usuarios.service.AuthenticationService;
import com.codigo.Gestion.Usuarios.service.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final ReniecClient reniecClient;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Value("${token.api}")
    private String tokenapi;


    /**
     * Registra un nuevo usuario con rol estándar.
     */
    @Override
    @Transactional
    public BaseResponse signUpUser(SignUpRequest signUpRequest) {
        BaseResponse response = new BaseResponse();

        try {
            UsuarioEntity usuario = getEntity(signUpRequest);
            usuario.setRoles(Collections.singleton(getRoles(Role.USER)));
            usuario.setIsEnabled(Constants.ESTADO_ACTIVO);
            usuario.setEmail(signUpRequest.getEmail());
            usuario.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));

            UsuarioEntity savedUsuario = usuarioRepository.save(usuario);

            response.setCode(Constants.OK_DNI_CODE); // Código de éxito
            response.setMessage("Usuario registrado exitosamente");
            response.setData(Optional.of(savedUsuario));
        } catch (Exception e) {
            response.setCode(Constants.ERROR_DNI_CODE); // Código de error
            response.setMessage("Error al registrar el usuario: " + e.getMessage());
            response.setData(Optional.empty());
        }
        return response;
    }

    /**
     * Registra un nuevo usuario con rol de administrador.
     */
    @Override
    @Transactional
    public BaseResponse signUpAdmin(SignUpRequest signUpRequest) {
        BaseResponse response = new BaseResponse();

        try {
            UsuarioEntity admin = getEntity(signUpRequest);
            admin.setRoles(Collections.singleton(getRoles(Role.ADMIN)));
            admin.setIsEnabled(Constants.ESTADO_ACTIVO);
            admin.setEmail(signUpRequest.getEmail());
            admin.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));

            // Guardar el admin en la base de datos
            UsuarioEntity savedAdmin = usuarioRepository.save(admin);

            // Crear la respuesta de éxito
            response.setCode(Constants.OK_DNI_CODE); // Código de éxito
            response.setMessage("Administrador registrado exitosamente");
            response.setData(Optional.of(savedAdmin));

        } catch (Exception e) {
            response.setCode(Constants.ERROR_DNI_CODE);
            response.setMessage("Error al registrar el administrador: " + e.getMessage());
            response.setData(Optional.empty());
        }

        return response;
    }


    /**
     * Obtiene el rol de usuario desde el repositorio.
     */
    private RolEntity getRoles(Role rolBuscado){
        return rolRepository.findByNombreRol(rolBuscado.name())
                .orElseThrow(() -> new RuntimeException("EL ROL BSUCADO NO EXISTE : "
                        + rolBuscado.name()));
    }

    /**
     * Ejecuta una consulta al servicio de Reniec para obtener datos de un usuario a partir de su documento.
     */
    private ReniecResponse executionReniec(String documento){
        String auth = "Bearer "+tokenapi;
        ReniecResponse response = reniecClient.getPersonaReniec(documento,auth);
        return response;
    }

    /**
     * Crea una entidad de usuario utilizando los datos obtenidos de Reniec.
     */
    private UsuarioEntity getEntity(SignUpRequest personaRequest){
        UsuarioEntity personaEntity = new UsuarioEntity();

        ReniecResponse response = executionReniec(personaRequest.getNumeroDocumento());
        if (Objects.nonNull(response)){
            personaEntity.setNombres(response.getNombres());
            personaEntity.setApellidoPaterno(response.getApellidoPaterno());
            personaEntity.setApellidoMaterno(response.getApellidoMaterno());
            personaEntity.setNumeroDocumento(response.getNumeroDocumento());
            personaEntity.setTipoDocumento(response.getTipoDocumento());
            personaEntity.setDigitoVerificador(response.getDigitoVerificador());

            return personaEntity;
        }
        return null;
    }

    @Override
    public SignInResponse signIn(SignInRequest signInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                signInRequest.getEmail(),signInRequest.getPassword()));
        var user = usuarioRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(()-> new IllegalArgumentException("ERROR USUARIO NO ENCONTRADO"));
        var token = jwtService.generateToken(user);
        SignInResponse response = new SignInResponse();
        response.setToken(token);
        return response;
    }

}
