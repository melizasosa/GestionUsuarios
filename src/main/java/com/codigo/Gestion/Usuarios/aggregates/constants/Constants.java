package com.codigo.Gestion.Usuarios.aggregates.constants;

public class Constants {
    public static final Boolean ESTADO_ACTIVO=true;
    public static final Integer REDIS_EXP = 5;
    public static final String REDIS_KEY_API_PERSON = "MS:APIS:EXTERNAS:";
    public static final String CLAVE_AccountNonExpired ="isAccountNonExpired";
    public static final String CLAVE_AccountNonLocked ="isAccountNonLocked";
    public static final String CLAVE_CredentialsNonExpired = "isCredentialsNonExpired";
    public static final String CLAVE_Enabled = "isEnabled";
    public static final Integer ERROR_DNI_CODE = 2004;
    public static final Integer OK_DNI_CODE = 2000;
    public static final String CLAIM_ROLE = "rol";
    public static final String ENPOINTS_PERMIT = "api/v1/authentication/**";
    public static final String ENPOINTS_USER = "api/v1/users/**";



}
