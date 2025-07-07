// src/main/java/pt/ul/fc/css/soccernow/dto/CampeonatoDTO.java
package pt.ul.fc.css.soccernow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import java.util.HashSet;

/**
 * DTO usado por los formularios y las vistas de Campeonatos.
 * <p>
 *  ▸ Campo "participanteIds" → nombre usado en la capa de servicio.  
 *  ▸ Alias "teamIds"         → nombre que esperan los templates Thymeleaf.
 * Ambos apuntan al mismo Set para evitar romper código existente.
 */
public class CampeonatoDTO {

    /* ──────────── SOLO LECTURA ──────────── */
    private Long id;

    /* ──────────── CAMPOS OBLIGATORIOS ──────────── */
    @NotBlank(message = "El nombre del campeonato no puede estar vacío")
    private String nome;

    @NotBlank(message = "La modalidad no puede estar vacía")
    private String modalidade;

    @NotBlank(message = "El formato no puede estar vacío")
    private String formato;

    /**
     * IDs de los equipos participantes (nombre interno).
     * Nunca debe ser null para que los <th:each> funcionen sin errores.
     */
    @NotEmpty(message = "Debe seleccionarse al menos un equipo participante")
    private Set<Long> participanteIds = new HashSet<>();

    /* ──────────── CONSTRUCTORES ──────────── */
    public CampeonatoDTO() {}

    /** Para filtros rápidos (id + nombre). */
    public CampeonatoDTO(Long id, String nome) {
        this(id, nome, null, null, new HashSet<>());
    }

    /** Constructor completo (incluye id). */
    public CampeonatoDTO(Long id,
                         String nome,
                         String modalidade,
                         String formato,
                         Set<Long> participanteIds) {
        this.id = id;
        this.nome = nome;
        this.modalidade = modalidade;
        this.formato = formato;
        if (participanteIds != null) this.participanteIds = participanteIds;
    }

    /** Para creación (sin id). */
    public CampeonatoDTO(String nome,
                         String modalidade,
                         String formato,
                         Set<Long> participanteIds) {
        this(null, nome, modalidade, formato, participanteIds);
    }

    /* ──────────── GETTERS / SETTERS ──────────── */
    public Long getId()                     { return id; }
    public void setId(Long id)              { this.id = id; }

    public String getNome()                 { return nome; }
    public void setNome(String nome)        { this.nome = nome; }

    public String getModalidade()           { return modalidade; }
    public void setModalidade(String mod)   { this.modalidade = mod; }

    public String getFormato()              { return formato; }
    public void setFormato(String formato)  { this.formato = formato; }

    public Set<Long> getParticipanteIds()               { return participanteIds; }
    public void setParticipanteIds(Set<Long> ids)       { this.participanteIds = ids; }

    /* ──────────── ALIAS PARA THYMELEAF ──────────── */
    /** Alias que usan los templates de formularios */
    public Set<Long> getTeamIds()            { return participanteIds; }
    public void setTeamIds(Set<Long> ids)    { this.participanteIds = ids; }

    /** Alias que usan los templates de la lista (`c.participantes`) */
    public Set<Long> getParticipantes()      { return participanteIds; }   
    public void setParticipantes(Set<Long> p){ this.participanteIds = p; } 
}
