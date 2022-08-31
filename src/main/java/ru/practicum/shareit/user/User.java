package ru.practicum.shareit.user;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "users")
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    @Email
    private String email;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
