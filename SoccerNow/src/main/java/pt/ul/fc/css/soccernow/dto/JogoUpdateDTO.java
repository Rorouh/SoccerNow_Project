package pt.ul.fc.css.soccernow.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

public class JogoUpdateDTO {
    @Future(message = "La fecha y hora debe ser futura")
    private LocalDateTime dateTime;

    private String location;
    private Boolean amigavel;

    @Size(min = 2, max = 2, message = "Deben especificarse exactamente 2 equipos")
    private Set<Long> teamIds;

    private Set<Long> refereeIds;
    private Long primaryRefereeId;

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Boolean getAmigavel() { return amigavel; }
    public void setAmigavel(Boolean amigavel) { this.amigavel = amigavel; }

    public Set<Long> getTeamIds() { return teamIds; }
    public void setTeamIds(Set<Long> teamIds) { this.teamIds = teamIds; }

    public Set<Long> getRefereeIds() { return refereeIds; }
    public void setRefereeIds(Set<Long> refereeIds) { this.refereeIds = refereeIds; }

    public Long getPrimaryRefereeId() { return primaryRefereeId; }
    public void setPrimaryRefereeId(Long primaryRefereeId) { this.primaryRefereeId = primaryRefereeId; }
}
