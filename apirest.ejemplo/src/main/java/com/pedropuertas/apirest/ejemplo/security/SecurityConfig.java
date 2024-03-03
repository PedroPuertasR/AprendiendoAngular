package com.pedropuertas.apirest.ejemplo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthEntryPoint entryPoint;


    /*@Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }*/

    //Bean para verificar la información de los usuarios que se logueen en la api
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

    //Configuración del CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        config.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    //Configuración del filtro de CORS
    @Bean
    public FilterRegistrationBean <CorsFilter> corsFilter(){
        FilterRegistrationBean <CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return bean;
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
                        .requestMatchers(HttpMethod.POST, "/api/clientes").hasAuthority("ADMIN") //Con esto se puede indicar que rol puede acceder a cada dirección
                        .requestMatchers(HttpMethod.POST, "/api/clientes/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE).hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT).hasAuthority("ADMIN")
                        .requestMatchers("/api/clientes/form").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/registro").permitAll()
                        .requestMatchers("/api/auth/registroAdmin").hasAuthority("ADMIN")
                        .anyRequest().permitAll()
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
