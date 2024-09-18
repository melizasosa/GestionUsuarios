package com.codigo.Gestion.Usuarios.service.impl;

import com.codigo.Gestion.Usuarios.aggregates.constants.Constants;
import com.codigo.Gestion.Usuarios.aggregates.request.SignUpRequest;
import com.codigo.Gestion.Usuarios.aggregates.response.ReniecResponse;
import com.codigo.Gestion.Usuarios.client.ReniecClient;
import com.codigo.Gestion.Usuarios.entity.RolEntity;
import com.codigo.Gestion.Usuarios.entity.Role;
import com.codigo.Gestion.Usuarios.entity.UsuarioEntity;
import com.codigo.Gestion.Usuarios.repository.RolRepository;
import com.codigo.Gestion.Usuarios.repository.UsuarioRepository;
import com.codigo.Gestion.Usuarios.service.AuthenticationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.util.*;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final ReniecClient reniecClient;
    @Value("${token.api}")
    private String tokenapi;

    public AuthenticationServiceImpl(UsuarioRepository usuarioRepository, ReniecClient reniecClient,RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.reniecClient = reniecClient;
        this.rolRepository = rolRepository;
    }

    /**
     * Registra un nuevo usuario con rol estándar.
     *
     * @param signUpRequest Objeto que contiene los datos del usuario a registrar.
     * @return UsuarioEntity registrado con rol de usuario.
     */
    @Override
    @Transactional
    public UsuarioEntity signUpUser(SignUpRequest signUpRequest) {
        UsuarioEntity usuario = getEntity(signUpRequest);

        //ASIGNADO FINALMENTE EL ROL ENCONTRADO AL USUARIO:
        usuario.setRoles(Collections.singleton(getRoles(Role.USER)));
        usuario.setIsEnabled(Constants.ESTADO_ACTIVO);
        usuario.setEmail(signUpRequest.getEmail());
        usuario.setPassword(signUpRequest.getPassword());
        return usuarioRepository.save(usuario);
    }

    /**
     * Registra un nuevo usuario con rol de administrador.
     *
     * @param signUpRequest Objeto que contiene los datos del usuario a registrar.
     * @return UsuarioEntity registrado con rol de administrador.
     */
    @Override
    @Transactional
    public UsuarioEntity signUpAdmin(SignUpRequest signUpRequest) {
        UsuarioEntity admin = getEntity(signUpRequest);
        admin.setIsEnabled(Constants.ESTADO_ACTIVO);
        admin.setEmail(signUpRequest.getEmail());
        admin.setRoles(Collections.singleton(getRoles(Role.ADMIN)));
        admin.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));
        return usuarioRepository.save(admin);
    }


    /**
     * Obtiene el rol de usuario desde el repositorio.
     *
     * @param rolBuscado Rol buscado (USER o ADMIN).
     * @return RolEntity correspondiente.
     * @throws RuntimeException si el rol no se encuentra.
     */
    private RolEntity getRoles(Role rolBuscado){
        return rolRepository.findByNombreRol(rolBuscado.name())
                .orElseThrow(() -> new RuntimeException("EL ROL BSUCADO NO EXISTE : "
                        + rolBuscado.name()));
    }

    /**
     * Ejecuta una consulta al servicio de Reniec para obtener datos de un usuario a partir de su documento.
     *
     * @param documento Número de documento del usuario.
     * @return ReniecResponse con los datos del usuario.
     */
    private ReniecResponse executionReniec(String documento){
        String auth = "Bearer "+tokenapi;
        ReniecResponse response = reniecClient.getPersonaReniec(documento,auth);
        return response;
    }

    /**
     * Crea una entidad de usuario utilizando los datos obtenidos de Reniec.
     *
     * @param personaRequest Objeto que contiene los datos básicos del usuario.
     * @return UsuarioEntity creado a partir de los datos de Reniec.
     */
    private UsuarioEntity getEntity(SignUpRequest personaRequest){
        UsuarioEntity personaEntity = new UsuarioEntity();

        //Ejecutar la consulta;
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


}
