// src/main/java/pt/ul/fc/css/soccernow/dto/CartaoCreateDTO.java
package pt.ul.fc.css.soccernow.dto;

import jakarta.validation.constraints.NotNull;

public class CartaoCreateDTO {
    @NotNull
    private String tipo;
    @NotNull
    private Long playerId;
    @NotNull
    private Long jogoId;

    public CartaoCreateDTO() {}

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
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
