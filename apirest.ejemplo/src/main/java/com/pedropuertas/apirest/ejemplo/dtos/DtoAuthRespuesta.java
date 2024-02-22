package com.pedropuertas.apirest.ejemplo.dtos;

import lombok.Data;

//Esta clase nos devolverá la información con el token y el tipo que tengas este
@Data
public class DtoAuthRespuesta {

    private String accessToken;
    private String tokenType = "Bearer ";

    public DtoAuthRespuesta(String accessToken) {
        this.accessToken = accessToken;
    }
}
