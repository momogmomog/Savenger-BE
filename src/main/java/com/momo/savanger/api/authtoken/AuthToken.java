package com.momo.savanger.api.authtoken;

import com.momo.savanger.api.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@ToString
@Table(name = "auth_tokens")
public class AuthToken {

    @Id
    @UuidGenerator
    private String id;

    private LocalDateTime lastAccessTime;

    @ManyToOne(targetEntity = User.class)
    @EqualsAndHashCode.Exclude
    private User user;
}
