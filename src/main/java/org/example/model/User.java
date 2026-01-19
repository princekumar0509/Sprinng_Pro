package org.example.model;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String id;
    String Username;
    String email;

    @Column(nullable = true)
    String role;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return Username; }
    public void setUsername(String username) { Username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}