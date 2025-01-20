package pt.laert.tracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "wallet")
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "serial")
    private Long id;

    @Column(name = "email")
    private String email;

    public WalletEntity(String email) {
        this.email = email;
    }

    public WalletEntity() {
        // No-arg constructor for JPA
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
