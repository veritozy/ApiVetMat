package com.itsqmet.service;

import com.itsqmet.component.JwtUtil;
import com.itsqmet.entity.Usuario;
import com.itsqmet.repository.UsuarioRepository;
import com.itsqmet.role.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil; // <--- NUEVA INYECCIÓN

    //LEER - Select * from usuarios;
    public List<Usuario> mostrarUsuarios() {
        return usuarioRepository.findAll();
    }

    //BUSCAR POR ID - Select * from usuarios where id=10;
    public Optional<Usuario> buscarUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    //GUARDAR - Insert into usuarios() values()
    public Usuario guardarUsuario(Usuario usuario) {
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        usuario.setRol(Rol.ROLE_VETERINARIO);
        return usuarioRepository.save(usuario);
    }

    //ACTUALIZAR - update usuarios set nombre='Cristian' where id=10;
    public Usuario actualizarUsuario(Long id, Usuario usuario) {
        Usuario usuarioExistente = buscarUsuarioById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setRol(usuario.getRol());
        if (usuario.getPassword() != null && !usuario.getPassword().equals(usuario.getPassword())) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuarioExistente);
    }

    //ELIMINAR - delete from usuarios where id=10
    public void eliminarUsuario(Long id) {
        Usuario usuarioEliminar = buscarUsuarioById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));
        usuarioRepository.delete(usuarioEliminar);
    }

    // --- NUEVO MÉTODO PARA EL LOGIN ---
    public Map<String, String> autenticar(Usuario loginUsuario) {
        // 1. Usamos findByEmail pero sin lanzar excepción inmediata para que no rompa el flujo
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(loginUsuario.getEmail());

        // 2. Verificamos si existe el usuario
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // 3. Verificamos la contraseña
            if (passwordEncoder.matches(loginUsuario.getPassword(), usuario.getPassword())) {
                String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol().name());

                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("email", usuario.getEmail());
                response.put("rol", usuario.getRol().name());
                return response;
            }
        }

        // Si llegamos aquí, es porque el usuario no existe O la contraseña es incorrecta
        return null;
    }

    //AUTENTICACIÓN
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        //Método builder para construir el objeto que representa el usuario autenticado
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(usuario.getRol().name())
                .build();
    }
}