// src/main/java/pt/ul/fc/css/soccernow/dto/CartaoDTO.java
package pt.ul.fc.css.soccernow.dto;

public class CartaoDTO {
    private Long id;
    private String tipo;
    private Long playerId;
    private Long jogoId;

    public CartaoDTO() {}

    public CartaoDTO(Long id, String tipo, Long playerId, Long jogoId) {
        this.id = id;
        this.tipo = tipo;
        this.playerId = playerId;
        this.jogoId = jogoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public static CartaoDTO fromEntity(pt.ul.fc.css.soccernow.domain.Cartao c) {
        return new CartaoDTO(
            c.getId(),
            c.getTipo(),
            c.getPlayer() != null ? c.getPlayer().getId() : null,
            c.getJogo()   != null ? c.getJogo().getId()   : null
        );
    }
}
