package com.codigo.Gestion.Usuarios.util;

import com.codigo.Gestion.Usuarios.aggregates.response.ReniecResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Util {
    public static String convertirAString(Object obj) {
        // Usa una librería como Jackson o Gson para convertir el objeto a JSON
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T convertirDesdeString(String json, Class<T> clazz) {
        // Usa una librería como Jackson o Gson para convertir JSON a objeto
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
