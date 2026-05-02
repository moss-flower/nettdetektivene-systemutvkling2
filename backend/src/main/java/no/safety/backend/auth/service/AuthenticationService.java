package no.safety.backend.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.safety.backend.auth.dto.UserLoginRequest;
import no.safety.backend.auth.dto.UserLoginResponse;
import no.safety.backend.auth.dto.UserLoginTokenResponse;
import no.safety.backend.auth.dto.TokenPairResponse;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.user.model.User;

/**
 * Service class that handles authentication-related business logic.
 * Provides methods for user login authentication and session refresh operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Authenticates a user login request and generates access and refresh tokens.
     * @param request the user login request containing email and password
     * @return a UserLoginTokenResponse containing user details and JWT tokens
     */
    public UserLoginTokenResponse authenticateLogin(UserLoginRequest request){
        log.info("AUTHSERVICE: Login Request");

        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(
            request.email(), 
            request.password()
        );
        
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        log.info("AUTH SERVICE: Authenticated");
        UserPrincipal userPrincipal = (UserPrincipal) authenticationResponse.getPrincipal();

        User user = userPrincipal.user();
        log.info("AUTHSERVICE: Token Generated");
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        UserLoginResponse userLoginResponse = UserLoginResponse.fromEntity(user, "Login successful.");

        return new UserLoginTokenResponse(userLoginResponse, accessToken, refreshToken);
    }

    /**
     * Refreshes an authentication session using a valid refresh token.
     * Generates new access and refresh token pairs for the user.
     * @param refreshToken the refresh token to validate and use for generating new tokens
     * @return a TokenPairResponse containing new access and refresh tokens
     */
    public TokenPairResponse refreshSession(String refreshToken) {
        if (refreshToken == null || !jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token.");
        }

        String email = jwtService.extractUsername(refreshToken);
        userDetailsService.loadUserByUsername(email);

        String newAccessToken = jwtService.generateAccessToken(email);
        String newRefreshToken = jwtService.generateRefreshToken(email);

        return new TokenPairResponse(newAccessToken, newRefreshToken);
    }
}
