// src/main/java/pt/ul/fc/css/soccernow/dto/JogoDTO.java
package pt.ul.fc.css.soccernow.dto;

import pt.ul.fc.css.soccernow.domain.Jogo;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JogoDTO {
    private Long id;
    private LocalDateTime dateTime;
    private String location;
    private boolean amigavel;
    private Integer homeScore;
    private Integer awayScore;
    private Long homeTeamId;
    private Long awayTeamId;
    private Long campeonatoId;
    private Set<Long> refereeIds;
    private Long primaryRefereeId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean cancelado;

    public JogoDTO() { }

    public static JogoDTO fromEntity(Jogo j) {
        JogoDTO d = new JogoDTO();
        d.setId(j.getId());
        d.setDateTime(j.getDateTime());
        d.setLocation(j.getLocation());
        d.setAmigavel(j.isAmigavel());
        d.setHomeScore(j.getHomeScore());
        d.setAwayScore(j.getAwayScore());
        d.setHomeTeamId(j.getHomeTeam().getId());
        d.setAwayTeamId(j.getAwayTeam().getId());
        d.setCampeonatoId(j.getCampeonato() != null ? j.getCampeonato().getId() : null);
        d.setRefereeIds(j.getReferees().stream()
                         .map(r -> r.getId())
                         .collect(Collectors.toSet()));
        d.setPrimaryRefereeId(
            j.getPrimaryReferee() != null ? j.getPrimaryReferee().getId() : null
        );
        d.setCancelado(j.isCancelado());
        return d;
    }
    // getters & setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isAmigavel() { return amigavel; }
    public void setAmigavel(boolean amigavel) { this.amigavel = amigavel; }

    public Integer getHomeScore() { return homeScore; }
    public void setHomeScore(Integer homeScore) { this.homeScore = homeScore; }

    public Integer getAwayScore() { return awayScore; }
    public void setAwayScore(Integer awayScore) { this.awayScore = awayScore; }

    public Long getHomeTeamId() { return homeTeamId; }
    public void setHomeTeamId(Long homeTeamId) { this.homeTeamId = homeTeamId; }

    public Long getAwayTeamId() { return awayTeamId; }
    public void setAwayTeamId(Long awayTeamId) { this.awayTeamId = awayTeamId; }

    public Long getCampeonatoId() { return campeonatoId; }
    public void setCampeonatoId(Long campeonatoId) { this.campeonatoId = campeonatoId; }

    public Set<Long> getArbitroIds() { return arbitroIds; }
    public void setArbitroIds(Set<Long> arbitroIds) { this.arbitroIds = arbitroIds; }

    public Long getPrimaryRefereeId() { return primaryRefereeId; }
    public void setPrimaryRefereeId(Long primaryRefereeId) { this.primaryRefereeId = primaryRefereeId; }

    public boolean isCancelado() { return cancelado; }
    public void setCancelado(boolean cancelado) { this.cancelado = cancelado; }
}
