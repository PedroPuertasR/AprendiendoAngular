package com.pedropuertas.apirest.ejemplo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthEntryPoint entryPoint;

    //Bean para verificar la información de los usuarios que se logueen en la api
    /*@Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }*/

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception{
        return authConfig.getAuthenticationManager();
    }

    //Con este Bean encriptamos todas las contraseñas
    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    //Este Bean incorporará el filtro de seguridad de JSON web token de nuestra clase JwtAuthFilter
    @Bean
    public JwtAuthFilter jwtAuthFilter(){
        return new JwtAuthFilter();
    }

    //Bean que establece una cadena de filtros de seguiradad en nuestra api. Aquí se determinan los permisos dependiendo de los roles
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        /* Forma antigüa de realizar el SecurityConfig

        http
                .csrf().disable() //Deshabilita un filtro que nos hará tener más errores
                .exceptionHandling() //Permite el manejo de excepciones
                .authenticationEntryPoint(entryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //Permite la gestión de excepciones
                .and()
                .authorizeHttpRequests() //Toda petición http debe ser autorizada
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic();*/

        http
                .csrf(AbstractHttpConfigurer::disable) //Deshabilita un filtro que nos hará tener más errores
                .authorizeHttpRequests(auth -> auth //Toda petición http debe ser autorizada
                        .requestMatchers(HttpMethod.GET, "/api/clientes").permitAll() //Con esto se puede indicar que rol puede acceder a cada dirección
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex //Permite el manejo de excepciones
                        .authenticationEntryPoint(entryPoint) //Establece el punto de entrada personalizado de autenticación para manejar las no autorizadas
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Permite la gestión de excepciones
                .httpBasic(Customizer.withDefaults());//Añade un filtro básico de http

        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class); //Añade nuestro filtro antes que el de http

        return http.build();
    }
}
