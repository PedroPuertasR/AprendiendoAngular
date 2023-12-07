package com.pedropuertas.apirest.ejemplo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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

    @NotEmpty(message = "no puedo estar vacío.")
    @Size(min = 4, message = "el tamaño debe estar entre 4 y 12 caracteres.")
    @Column(nullable = false)
    private String nombre;

    @NotEmpty(message = "no puedo estar vacío.")
    private String apellidos;

    @NotEmpty(message = "no puedo estar vacío.")
    @Email(message = "debe tener un formato válido.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull(message = "no puede estar vacío.")
    @Column(name = "create_at")
    @Temporal(TemporalType.DATE)
    private LocalDate createAt;


    private String foto;

    /*No es necesario porque se utiliza un DatePicker en el front
    @PrePersist
    public void prePersist(){
        createAt = LocalDate.now();
    }*/

}
