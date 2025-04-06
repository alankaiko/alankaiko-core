package br.com.alankaiko.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datacadastro;

    private Boolean bloqueioEdicao;

    private Long idUsuarioBloqueio;

    private LocalDate dataBloqueio;

    @PrePersist
    public void atualizarData() {
        if (this.datacadastro == null)
            this.datacadastro = LocalDate.now();
    }

}
