package com.itsqmet.controller;

import com.itsqmet.entity.Usuario;
import com.itsqmet.repository.UsuarioRepository;
import com.itsqmet.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public Usuario login(@RequestBody Usuario loginUsuario){
        Usuario usuario = usuarioRepository.findByEmail(loginUsuario.getEmail())
                .orElse(null);

        if(usuario != null && passwordEncoder.matches(loginUsuario.getPassword(), usuario.getPassword())){
            return  usuario;
        }
        return null;
    }
}
