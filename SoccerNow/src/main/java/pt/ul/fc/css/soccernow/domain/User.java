package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // Campo opcional apenas para facilitar testes – pode ser mapeado conforme necessidade
    private String password;

    // Getters y setters
    public Long getId() {
        return id;
    }

    // Setter adicionado apenas para facilitar testes unitários (não deve ser usado em produção)
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

    // Construtores auxiliares para facilitar criação em testes
    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String name, String email, String password) {
        this(name, email);
        this.password = password;
    }
}
