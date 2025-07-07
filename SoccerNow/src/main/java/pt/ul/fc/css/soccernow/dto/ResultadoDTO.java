package pt.ul.fc.css.soccernow.dto;

import java.util.List;

/**  Representa el JSON que llega desde GameResultController. */
public class ResultadoDTO {
    public int  homeScore;
    public int  awayScore;
    public Long winnerId;           // opcional
    public List<CartaoDTO> cartoes; // opcional

    public static class CartaoDTO {
        public Long   playerId;     // id del jugador
        public String tipo;         // "AMARILLA" | "ROJA"
    }
}
