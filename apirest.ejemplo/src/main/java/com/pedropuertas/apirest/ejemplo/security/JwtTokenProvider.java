package com.pedropuertas.apirest.ejemplo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtTokenProvider {

    //Método para generar el Token por medio de la autenticación
    public String generarToken(Authentication auth){
        String username = auth.getName();
        Date tiempoActual = new Date();
        Date expiracionToken = new Date(tiempoActual.getTime() + ConstSecurity.JWT_EXPIRATION_TOKEN);

        byte[] keyBytes = Decoders.BASE64.decode(ConstSecurity.JWT_FIRMA);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        //Línea donde se genera el token
        return Jwts.builder()
                .subject(username)
                .issuedAt(tiempoActual)
                .expiration(expiracionToken)
                .signWith(key)
                .compact();
    }

    private SecretKey generarSK(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(ConstSecurity.JWT_FIRMA));
    }

    //Método para extraer username a partir de un token
    public String obtenerUsername(String token){
        Claims claims = Jwts.parser()
                .verifyWith(generarSK())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    //Método para validar el token
    public Boolean validarToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(generarSK())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (Exception e){
            throw new AuthenticationCredentialsNotFoundException("Jwt ha expirado o está incorrecto.");
        }
    }

}
