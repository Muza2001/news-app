package digital.one.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtEncoder jwtEncoder;

    public String generateToken(User user){
        return generateTokenWithUsername(user.getUsername());
    }

    public String generateTokenWithUsername(String username) {
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.
                builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(getJwtExpirationData()))
                .subject(username)
                .claim("scope", "ROLE_USER")
                .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }
    public Long getJwtExpirationData(){
        return 18_000_000L;
    }
}
