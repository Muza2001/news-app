package digital.one.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@NonNull
@Builder
@Table(name = "users")
public class User {

    public User(String full_name, String password, String username, String email, Instant expiration, Boolean isEnabled) {
        this.full_name = full_name;
        this.password = password;
        this.username = username;
        this.email = email;
        this.expiration = expiration;
        this.isEnabled = isEnabled;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String full_name;

    private String password;

    @Column(unique = true, name = "username")
    private String username;

    @Column(unique = true, name = "email")
    private String email;

    private Instant expiration;

    private Boolean isEnabled;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}