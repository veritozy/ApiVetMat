package com.itsqmet.controller;

import com.itsqmet.entity.Usuario;
import com.itsqmet.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    //LEER /usuarios
    @GetMapping
    public List<Usuario> getUsuarios(){
        return usuarioService.mostrarUsuarios();
    }

    //GUARDAR
    @PostMapping("/registrarUsuario")
    public Usuario postUsuario(@RequestBody Usuario usuario){
        return usuarioService.guardarUsuario(usuario);
    }

    //ACTUALIZAR
    @PutMapping("/{id}")
    public Usuario putUsuario(@PathVariable Long id, @RequestBody Usuario usuario){
        return usuarioService.actualizarUsuario(id, usuario);
    }

 //ELIMINAR
    @DeleteMapping("/{id}")
    public void deleteUsuario(@PathVariable Long id){
        usuarioService.eliminarUsuario(id);
    }

    //BUSCAR ID
    @GetMapping("/{id}")
    public Usuario getUsuarioById(@PathVariable Long id){
        return usuarioService.buscarUsuarioById(id)
                .orElseThrow(()-> new RuntimeException("Usuario no encontrado"));
    }

}
