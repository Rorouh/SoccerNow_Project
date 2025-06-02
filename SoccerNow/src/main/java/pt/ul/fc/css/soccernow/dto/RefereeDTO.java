// src/main/java/pt/ul/fc/css/soccernow/dto/RefereeDTO.java
package pt.ul.fc.css.soccernow.dto;

public class RefereeDTO {
    private Long id;
    private String name;
    private String email;
    private boolean certified;

    public RefereeDTO() { }

    public RefereeDTO(Long id, String name, String email, boolean certified) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.certified = certified;
    }

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isCertified() { return certified; }
    public void setCertified(boolean certified) { this.certified = certified; }
}
