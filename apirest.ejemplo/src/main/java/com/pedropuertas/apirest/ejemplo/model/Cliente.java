package com.pedropuertas.apirest.ejemplo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Cliente{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellidos;
    private String email;

    @Column(name = "create_at")
    @Temporal(TemporalType.DATE)
    private LocalDate createAt;

    @PrePersist
    public void prePersist(){
        createAt = LocalDate.now();
    }

}
