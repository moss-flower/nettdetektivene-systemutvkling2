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

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

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
