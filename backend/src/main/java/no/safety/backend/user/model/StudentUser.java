package no.safety.backend.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id")
public class StudentUser extends User {
    public StudentUser(){
        super();
    }

    public StudentUser(String username, String email, String passwordHash) {
        super(username, email, passwordHash);
    }
}
