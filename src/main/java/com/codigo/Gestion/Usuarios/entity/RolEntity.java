package com.codigo.Gestion.Usuarios.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "rol")
@Data
public class RolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;
    private String nombreRol;
}
