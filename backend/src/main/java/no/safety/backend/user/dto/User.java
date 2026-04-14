package no.safety.backend.user.dto;

public record User(
    String username,
    String passwordHash
) {
} 
