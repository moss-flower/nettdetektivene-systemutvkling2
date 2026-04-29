package no.safety.backend.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.safety.backend.auth.dto.UserLoginRequest;
import no.safety.backend.auth.dto.UserLoginResponse;
import no.safety.backend.auth.dto.UserLoginTokenResponse;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.user.model.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

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

        String jwtToken = jwtService.generateToken(user.getEmail());
        log.info("AUTHSERVICE: Token Generated");

        UserLoginResponse userLoginResponse = UserLoginResponse.fromEntity(user, "Login successful.");

        return new UserLoginTokenResponse(userLoginResponse, jwtToken);
    }
}
