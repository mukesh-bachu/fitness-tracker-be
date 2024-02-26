package com.app.FitbitDashBoard.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.concurrent.ThreadLocalRandom;
import javax.validation.constraints.*;
import java.util.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users",
        uniqueConstraints=
@UniqueConstraint(columnNames={"EMAIL"}))

public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits")
    private String phone;


    private String profilePic;
    private String country;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private Integer steps;
    private Integer calories;

    @PrePersist
    protected void onCreate() {
        steps = ThreadLocalRandom.current().nextInt(100, 10001);
        calories = ThreadLocalRandom.current().nextInt(100, 2001);
    }

    @Enumerated(EnumType.STRING)
    private Role role;


    @ElementCollection(fetch = FetchType.EAGER) // Use ElementCollection for a collection of Embeddable or basic types
    @CollectionTable(name = "user_following", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "following_email")
    private Set<String> following = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_followers", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "follower_email")
    private Set<String> followers = new HashSet<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
