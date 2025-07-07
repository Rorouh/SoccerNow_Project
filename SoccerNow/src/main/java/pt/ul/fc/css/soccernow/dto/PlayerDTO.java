// src/main/java/pt/ul/fc/css/soccernow/dto/PlayerDTO.java
package pt.ul.fc.css.soccernow.dto;

import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.User.PreferredPosition;

public class PlayerDTO {

    private Long id;
    private String name;
    private String email;

    /** NUEVO → solo se usa en formularios */
    private String password;

    private PreferredPosition preferredPosition;
    private Integer goals;
    private Integer cards;
    private Integer games;          // calculado en fromEntity

    /* ---------- factory ---------- */
    public static PlayerDTO fromEntity(Player p) {
        PlayerDTO dto = new PlayerDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setEmail(p.getEmail());
        dto.setPreferredPosition(p.getPreferredPosition());
        dto.setGoals(p.getGoals());
        dto.setCards(p.getCards());
        dto.setGames(p.getGames());   // método que creaste en Player
        return dto;
    }

    /* ---------- getters / setters ---------- */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /* NUEVOS (password) */
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public PreferredPosition getPreferredPosition() { return preferredPosition; }
    public void setPreferredPosition(PreferredPosition preferredPosition) {
        this.preferredPosition = preferredPosition;
    }

    public Integer getGoals() { return goals; }
    public void setGoals(Integer goals) { this.goals = goals; }

    public Integer getCards() { return cards; }
    public void setCards(Integer cards) { this.cards = cards; }

    public Integer getGames() { return games; }
    public void setGames(Integer games) { this.games = games; }
}
