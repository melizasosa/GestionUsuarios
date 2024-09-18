package com.codigo.Gestion.Usuarios.aggregates.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequest {
    private String numdoc;
    private String nombres;
    private String apellidos;
}
