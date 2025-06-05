// src/main/java/pt/ul/fc/css/soccernow/dto/CampeonatoDTO.java
package pt.ul.fc.css.soccernow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public class CampeonatoDTO {

    // Solo de lectura en respuestas
    private Long id;

    // Construtor compacto para filtros
    public CampeonatoDTO(Long id, String nome) {
        this.id = id;
        this.nome = nome;
        this.modalidade = null;
        this.formato = null;
        this.participanteIds = null;
    }

    @NotBlank(message = "El nombre del campeonato no puede estar vacío")
    private String nome;

    @NotBlank(message = "La modalidad no puede estar vacía")
    private String modalidade;

    @NotBlank(message = "El formato no puede estar vacío")
    private String formato;

    /**
     * IDs de los equipos participantes.
     * En creación/actualización, debe enviarse al menos uno.
     */
    @NotEmpty(message = "Debe enviarse al menos un equipo participante")
    private Set<Long> participanteIds;

    public CampeonatoDTO() { }

    /** Constructor completo (respuestas) */
    public CampeonatoDTO(Long id, String nome, String modalidade, String formato, Set<Long> participanteIds) {
        this.id = id;
        this.nome = nome;
        this.modalidade = modalidade;
        this.formato = formato;
        this.participanteIds = participanteIds;
    }

    /** Constructor sin id (para creación) */
    public CampeonatoDTO(String nome, String modalidade, String formato, Set<Long> participanteIds) {
        this(null, nome, modalidade, formato, participanteIds);
    }

    // --- Getters y setters ---
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getModalidade() {
        return modalidade;
    }
    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public String getFormato() {
        return formato;
    }
    public void setFormato(String formato) {
        this.formato = formato;
    }

    public Set<Long> getParticipanteIds() {
        return participanteIds;
    }
    public void setParticipanteIds(Set<Long> participanteIds) {
        this.participanteIds = participanteIds;
    }
}
