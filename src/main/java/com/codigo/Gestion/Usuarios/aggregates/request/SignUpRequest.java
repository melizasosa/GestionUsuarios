package com.codigo.Gestion.Usuarios.aggregates.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {

    private String email;
    private String password;
    private String numeroDocumento;
    private String nombres;
}
