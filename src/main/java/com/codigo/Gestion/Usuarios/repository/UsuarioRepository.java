package com.codigo.Gestion.Usuarios.repository;

import com.codigo.Gestion.Usuarios.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByNumeroDocumento(String numeroDocumento);
    Optional<UsuarioEntity> findByEmail(String email);
    // Nuevo método para encontrar solo usuarios activos
    List<UsuarioEntity> findAllByIsEnabledTrue();
}
