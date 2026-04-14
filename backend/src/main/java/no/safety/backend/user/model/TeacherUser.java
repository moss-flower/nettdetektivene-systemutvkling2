package no.safety.backend.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "teachers")
@PrimaryKeyJoinColumn(name = "user_id")
public class TeacherUser extends User {
    public TeacherUser(){
        super();
    }

    public TeacherUser(String username, String email, String passwordHash) {
        super(username, email, passwordHash);
    }
}
