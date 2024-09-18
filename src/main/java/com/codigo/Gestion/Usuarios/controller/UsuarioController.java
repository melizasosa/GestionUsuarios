package com.codigo.Gestion.Usuarios.controller;

import com.codigo.Gestion.Usuarios.aggregates.request.SignUpRequest;
import com.codigo.Gestion.Usuarios.entity.UsuarioEntity;
import com.codigo.Gestion.Usuarios.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    @GetMapping("/{dni}")
    public ResponseEntity<UsuarioEntity> getUserByDni(@PathVariable String dni) {
            return ResponseEntity.ok(usuarioService.getUserByDni(dni));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioEntity>> getAllUsers() {
        return ResponseEntity.ok(usuarioService.getAllUsers());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UsuarioEntity> updateUser(@PathVariable Long id, @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(usuarioService.updateUser(id, signUpRequest));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        usuarioService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


}
