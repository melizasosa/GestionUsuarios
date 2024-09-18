package com.codigo.Gestion.Usuarios.repository;

import com.codigo.Gestion.Usuarios.entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<RolEntity,Long> {
    Optional<RolEntity> findByNombreRol(String rol);
}
