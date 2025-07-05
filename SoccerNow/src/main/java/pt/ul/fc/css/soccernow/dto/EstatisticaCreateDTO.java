// src/main/java/pt/ul/fc/css/soccernow/dto/EstatisticaCreateDTO.java
package pt.ul.fc.css.soccernow.dto;

import jakarta.validation.constraints.NotNull;

public class EstatisticaCreateDTO {
    @NotNull
    private Integer gols;
    @NotNull
    private Long playerId;
    @NotNull
    private Long jogoId;

    public EstatisticaCreateDTO() {}

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
}
