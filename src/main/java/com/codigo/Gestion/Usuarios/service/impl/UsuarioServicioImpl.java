package com.codigo.Gestion.Usuarios.service.impl;

import com.codigo.Gestion.Usuarios.aggregates.constants.Constants;
import com.codigo.Gestion.Usuarios.aggregates.request.SignUpRequest;
import com.codigo.Gestion.Usuarios.aggregates.response.BaseResponse;
import com.codigo.Gestion.Usuarios.client.ReniecClient;
import com.codigo.Gestion.Usuarios.entity.UsuarioEntity;
import com.codigo.Gestion.Usuarios.exception.ResourceNotFoundException;
import com.codigo.Gestion.Usuarios.redis.RedisService;
import com.codigo.Gestion.Usuarios.repository.UsuarioRepository;
import com.codigo.Gestion.Usuarios.service.UsuarioService;
import com.codigo.Gestion.Usuarios.util.Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementación de la lógica de negocio relacionada con los usuarios.
 */
@Service
@RequiredArgsConstructor
public class UsuarioServicioImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RedisService redisService;

    @Value("${token.api}")
    private String tokenapi;

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username)
                    throws UsernameNotFoundException {
                return usuarioRepository.findByEmail(username).orElseThrow(
                        ()-> new UsernameNotFoundException("USUARIO NO ENCONTRADO"));
            }
        };
    }

    /**
     * Obtiene un usuario por su número de documento (DNI).
     * Primero busca en Redis y, si no lo encuentra, lo busca en la base de datos.
     */
    @Override
    public BaseResponse getUserByDni(String dni) {

        BaseResponse response = new BaseResponse();

        try {
            String redisKey = Constants.REDIS_KEY_API_PERSON + dni;

            // Obtener datos de Redis
            Optional<UsuarioEntity> cachedUser = obtenerUsuarioDesdeRedis(redisKey);
            if (cachedUser.isPresent()) {
                response.setCode(Constants.OK_DNI_CODE); // Código de éxito
                response.setMessage("Usuario encontrado en caché Redis");
                response.setData(Optional.of(cachedUser.get()));
                return response;
            }

            // Obtener datos de la base de datos si no están en Redis
            UsuarioEntity usuario = obtenerUsuarioDesdeBaseDeDatos(dni);

            // Almacenar datos en Redis para futuras consultas
            almacenarUsuarioEnRedis(redisKey, usuario);
            response.setCode(Constants.OK_DNI_CODE); // Código de éxito
            response.setMessage("Usuario encontrado en la base de datos");
            response.setData(Optional.of(usuario));

        } catch (Exception e) {
            // Manejar cualquier error inesperado
            response.setCode(Constants.ERROR_DNI_CODE); // Código de error
            response.setMessage("Error al buscar el usuario: " + e.getMessage());
            response.setData(Optional.empty());
        }

        return response;
    }

    /**
     * Obtiene todos los usuarios activos de la base de datos.
     */
    @Override
    public List<UsuarioEntity> getAllUsers() {
        return usuarioRepository.findAllByIsEnabledTrue();
    }


    /**
     * Actualiza los detalles de un usuario existente.
     */
    @Override
    @Transactional
    public BaseResponse updateUser(Long id, SignUpRequest signUpRequest) {

        BaseResponse response = new BaseResponse();

        // Validar los datos proporcionados
        if (id == null || signUpRequest == null || signUpRequest.getEmail() == null || signUpRequest.getNumeroDocumento() == null) {
            throw new IllegalArgumentException("Los datos proporcionados no son válidos. Por favor, verifica la información enviada.");
        }

        // Lógica existente para actualizar el usuario
        UsuarioEntity existingUser = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        existingUser.setNombres(signUpRequest.getNombres());

        // Actualizar otros campos si es necesario
        UsuarioEntity updatedUser = usuarioRepository.save(existingUser);
        response.setCode(Constants.OK_DNI_CODE); // Código de éxito
        response.setMessage("Usuario actualizado exitosamente");
        response.setData(Optional.of(updatedUser));

        return response;
    }


    /**
     * Marca un usuario como deshabilitado en lugar de eliminarlo físicamente de la base de datos.
     */
    @Override
    @Transactional
    public BaseResponse deleteUser(Long id) {
        BaseResponse response = new BaseResponse();

        UsuarioEntity existingUser = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Cambiar el estado del usuario en lugar de eliminarlo
        existingUser.setIsEnabled(false); // Marcar al usuario como deshabilitado
        usuarioRepository.save(existingUser); // Guardar el usuario con el nuevo estado
        response.setCode(Constants.OK_DNI_CODE);
        response.setMessage("Usuario eliminado exitosamente");
        response.setData(Optional.of(existingUser));
        return response;
    }


    /**
     * Obtiene un usuario desde Redis utilizando una clave.
    */
    private Optional<UsuarioEntity> obtenerUsuarioDesdeRedis(String redisKey) {
        String cachedUserInfo = redisService.getDataDesdeRedis(redisKey);
        if (Objects.nonNull(cachedUserInfo)) {
            return Optional.of(Util.convertirDesdeString(cachedUserInfo, UsuarioEntity.class));
        }
        return Optional.empty();
    }

    /**
     * Obtiene un usuario desde la base de datos utilizando su DNI.
      */
    private UsuarioEntity obtenerUsuarioDesdeBaseDeDatos(String dni) {
        return usuarioRepository.findByNumeroDocumento(dni)
                .orElseThrow(() -> new ResourceNotFoundException("El recurso solicitado no fue encontrado. " ));
    }

    /**
     * Almacena los datos de un usuario en Redis para acelerar futuras consultas.
     */
    private void almacenarUsuarioEnRedis(String redisKey, UsuarioEntity usuario) {
        String userInfoForRedis = Util.convertirAString(usuario);
        redisService.guardarEnRedis(redisKey, userInfoForRedis, Constants.REDIS_EXP);
    }
}
