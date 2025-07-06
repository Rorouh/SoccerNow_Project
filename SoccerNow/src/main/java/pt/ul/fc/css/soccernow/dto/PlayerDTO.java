package pt.ul.fc.css.soccernow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.User.PreferredPosition;

public class PlayerDTO {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String name;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String email;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private PreferredPosition preferredPosition;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private int goals;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private int cards;

  public PlayerDTO() {}

  public PlayerDTO(
      Long id, String name, String email, String password, PreferredPosition preferredPosition) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.preferredPosition = preferredPosition;
  }

  // Constructor completo
  public PlayerDTO(
      Long id,
      String name,
      String email,
      PreferredPosition preferredPosition,
      int goals,
      int cards) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.preferredPosition = preferredPosition;
    this.goals = goals;
    this.cards = cards;
  }

  public PlayerDTO(
      String name, String email, String password, PreferredPosition preferredPosition) {
    this(null, name, email, password, preferredPosition);
  }

  /** Factory para mapear entidad → DTO de salida */
  public static PlayerDTO fromEntity(Player p) {
    return new PlayerDTO(
        p.getId(), p.getName(), p.getEmail(), p.getPreferredPosition(), p.getGoals(), p.getCards());
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

  public PreferredPosition getPreferredPosition() {
    return preferredPosition;
  }

  public void setPreferredPosition(PreferredPosition preferredPosition) {
    this.preferredPosition = preferredPosition;
  }

  public Integer getGoals() {
    return goals;
  }

  public void setGoals(Integer goals) {
    this.goals = goals;
  }

  public Integer getCards() {
    return cards;
  }

  public void setCards(Integer cards) {
    this.cards = cards;
  }
}
