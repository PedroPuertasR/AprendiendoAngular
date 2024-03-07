package com.pedropuertas.apirest.ejemplo.dtos;

import lombok.Data;

//Esta clase nos devolverá la información con el token y el tipo que tengas este
@Data
public class DtoAuthRespuesta {

    private String accessToken;
    private String tokenType = "Bearer ";
    private String username;

    public DtoAuthRespuesta(String accessToken, String username) {
        this.accessToken = accessToken;
        this.username = username;
    }
}
