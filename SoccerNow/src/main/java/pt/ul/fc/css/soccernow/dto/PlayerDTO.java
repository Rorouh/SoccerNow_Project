package pt.ul.fc.css.soccernow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class PlayerDTO {


    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String email;

    /** Nunca devolvemos la contraseña en la respuesta */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private PreferredPosition preferredPosition;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int goals;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int cards;

    public PlayerDTO() { }

    public PlayerDTO(Long id, String name, String email, String password, String preferredPosition) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.preferredPosition = preferredPosition;
    }


    public PlayerDTO(Long id, String name, String email, String password, String preferredPosition, int goals, int cards) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.preferredPosition = preferredPosition;
        this.goals = goals;
        this.cards = cards;
    }

    public PlayerDTO(String name, String email, String password, String preferredPosition) {
        this(null, name, email, password, preferredPosition);
    }

    /** Factory para mapear entidad → DTO de salida */
    public static PlayerDTO fromEntity(Player p) {
        return new PlayerDTO(
            p.getId(),
            p.getName(),
            p.getEmail(),
            p.getPreferredPosition(),
            p.getGoals(),
            p.getCards()
        );
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPreferredPosition() {
        return preferredPosition;
    }

    public void setPreferredPosition(String preferredPosition) {
        this.preferredPosition = preferredPosition;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getCards() {
        return cards;
    }

    public void setCards(int cards) {
        this.cards = cards;
    }
}
