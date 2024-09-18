package com.codigo.Gestion.Usuarios.service.impl;

import com.codigo.Gestion.Usuarios.aggregates.constants.Constants;
import com.codigo.Gestion.Usuarios.aggregates.request.SignUpRequest;
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
    private final ReniecClient reniecClient;

    // Inyectar el token de API desde el archivo de configuración
    @Value("${token.api}")
    private String tokenapi;

    /**
     * Obtiene un usuario por su número de documento (DNI).
     * Primero busca en Redis y, si no lo encuentra, lo busca en la base de datos.
     *
     * @param dni Número de documento del usuario.
     * @return UsuarioEntity encontrado por su DNI.
     */
    @Override
    public UsuarioEntity getUserByDni(String dni) {
       /* return usuarioRepository.findByNumeroDocumento(dni)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con DNI: " + dni));*/

        String redisKey = Constants.REDIS_KEY_API_PERSON + dni;

        // Obtener datos de Redis
        Optional<UsuarioEntity> cachedUser = obtenerUsuarioDesdeRedis(redisKey);
        if (cachedUser.isPresent()) {
            return cachedUser.get();
        }

        // Obtener datos de la base de datos si no están en Redis
        UsuarioEntity usuario = obtenerUsuarioDesdeBaseDeDatos(dni);

        // Almacenar datos en Redis para futuras consultas
        almacenarUsuarioEnRedis(redisKey, usuario);

        return usuario;
    }

    /**
     * Obtiene todos los usuarios activos de la base de datos.
     *
     * @return Lista de todas las entidades de usuario activas.
     */
    @Override
    public List<UsuarioEntity> getAllUsers() {
        return usuarioRepository.findAllByIsEnabledTrue();
    }


    /**
     * Actualiza los detalles de un usuario existente.
     *
     * @param id            ID del usuario a actualizar.
     * @param signUpRequest Objeto con los datos actualizados del usuario.
     * @return UsuarioEntity actualizado.
     * @throws IllegalArgumentException si los datos proporcionados no son válidos.
     */
    @Override
    @Transactional
    public UsuarioEntity updateUser(Long id, SignUpRequest signUpRequest) {

        // Validar los datos proporcionados
        if (id == null || signUpRequest == null || signUpRequest.getEmail() == null || signUpRequest.getNumeroDocumento() == null) {
            throw new IllegalArgumentException("Los datos proporcionados no son válidos. Por favor, verifica la información enviada.");
        }

        // Lógica existente para actualizar el usuario
        UsuarioEntity existingUser = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        existingUser.setEmail(signUpRequest.getEmail());
        existingUser.setNumeroDocumento(signUpRequest.getNumeroDocumento());
        existingUser.setPassword(signUpRequest.getPassword());
        existingUser.setNumeroDocumento(signUpRequest.getNumeroDocumento());
        // Actualizar otros campos si es necesario
        return usuarioRepository.save(existingUser);
    }


    /**
     * Marca un usuario como deshabilitado en lugar de eliminarlo físicamente de la base de datos.
     *
     * @param id ID del usuario a deshabilitar.
     */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        UsuarioEntity existingUser = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Cambiar el estado del usuario en lugar de eliminarlo
        existingUser.setIsEnabled(false); // Marcar al usuario como deshabilitado
        usuarioRepository.save(existingUser); // Guardar el usuario con el nuevo estado
    }


    /**
     * Obtiene un usuario desde Redis utilizando una clave.
     *
     * @param redisKey Clave de Redis para buscar los datos del usuario.
     * @return UsuarioEntity obtenido de Redis, si está presente.
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
     *
     * @param dni Número de documento del usuario.
     * @return UsuarioEntity obtenido de la base de datos.
     * @throws ResourceNotFoundException si el usuario no se encuentra en la base de datos.
     */
    private UsuarioEntity obtenerUsuarioDesdeBaseDeDatos(String dni) {
        return usuarioRepository.findByNumeroDocumento(dni)
                .orElseThrow(() -> new ResourceNotFoundException("El recurso solicitado no fue encontrado. " ));
    }

    /**
     * Almacena los datos de un usuario en Redis para acelerar futuras consultas.
     *
     * @param redisKey Clave de Redis para almacenar los datos del usuario.
     * @param usuario  Entidad del usuario a almacenar en Redis.
     */
    private void almacenarUsuarioEnRedis(String redisKey, UsuarioEntity usuario) {
        String userInfoForRedis = Util.convertirAString(usuario);
        redisService.guardarEnRedis(redisKey, userInfoForRedis, Constants.REDIS_EXP);
    }
}
