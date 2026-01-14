package com.BharatCrypto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.BharatCrypto.domain.USER_ROLE;
import com.BharatCrypto.domain.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    // @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    // @NotBlank(message = "Password is required") // Comment out to allow updates without re-setting password
    private String password;

    @Pattern(regexp = "^[a-zA-Z\\s.]*$", message = "Full name should only contain letters, spaces, and dots")
    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Pattern(regexp = "^$|^(\\+91|0)?[6-9][0-9]{9}$", message = "Invalid Indian mobile number. Must be 10 digits starting with 6-9, optional +91/0")
    @Size(max = 15, message = "Mobile number cannot exceed 15 characters")
    private String mobile;

    @Size(max = 50, message = "Date of birth cannot exceed 50 characters")
    private String dateOfBirth;

    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Nationality should only contain letters")
    @Size(max = 50, message = "Nationality cannot exceed 50 characters")
    private String nationality;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "City should only contain letters")
    @Size(max = 50, message = "City cannot exceed 50 characters")
    private String city;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid Indian PIN code (must be 6 digits)")
    @Size(max = 12, message = "Postcode cannot exceed 12 characters")
    private String postcode;

    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Country should only contain letters")
    @Size(max = 50, message = "Country cannot exceed 50 characters")
    private String country;

    private UserStatus status = UserStatus.PENDING;

    private boolean verified = false;

    @Embedded
    private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();

    @Column(columnDefinition = "LONGTEXT")
    private String picture;

    private USER_ROLE role = USER_ROLE.ROLE_USER;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevents infinite recursion in JSON serialization
    private Watchlist watchlist;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.toString()));
        return authorities;
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
