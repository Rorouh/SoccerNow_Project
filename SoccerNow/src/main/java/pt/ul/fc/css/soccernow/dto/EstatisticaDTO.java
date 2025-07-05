// src/main/java/pt/ul/fc/css/soccernow/dto/EstatisticaDTO.java
package pt.ul.fc.css.soccernow.dto;

public class EstatisticaDTO {
    private Long id;
    private Integer gols;
    private Long playerId;
    private Long jogoId;

    public EstatisticaDTO() {}

    public EstatisticaDTO(Long id, Integer gols, Long playerId, Long jogoId) {
        this.id = id;
        this.gols = gols;
        this.playerId = playerId;
        this.jogoId = jogoId;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGols() {
        return gols;
    }
    public void setGols(Integer gols) {
        this.gols = gols;
    }

    public Long getPlayerId() {
        return playerId;
    }
    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getJogoId() {
        return jogoId;
    }
    public void setJogoId(Long jogoId) {
        this.jogoId = jogoId;
    }

    public static EstatisticaDTO fromEntity(pt.ul.fc.css.soccernow.domain.Estatisticas e) {
        return new EstatisticaDTO(
            e.getId(),
            e.getGols(),
            e.getPlayer() != null ? e.getPlayer().getId() : null,
            e.getJogo()   != null ? e.getJogo().getId()   : null
        );
    }
}
