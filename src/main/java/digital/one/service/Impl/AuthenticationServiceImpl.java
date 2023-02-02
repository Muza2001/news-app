package digital.one.service.Impl;

import digital.one.dto.AuthResponse;
import digital.one.dto.AuthenticationRequestDto;
import digital.one.dto.AuthenticationResponse;
import digital.one.dto.UserDto;
import digital.one.model.User;
import digital.one.repository.UserRepository;
import digital.one.security.JwtProvider;
import digital.one.service.AuthenticationService;
import digital.one.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService, UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final RefreshTokenService refreshTokenService;

    private final JwtProvider jwtProvider;

    private final AuthenticationManager authenticationManager;
    @Override
    public ResponseEntity<?> register(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setExpiration(Instant.now());
        user.setFull_name(user.getFull_name());
        user.setPassword(encoder.encode(userDto.getPassword()));
        user.setIsEnabled(true);
        userRepository.save(user);
        return ResponseEntity.status(201).body("Successfully created");
    }

    @Override
    public ResponseEntity<?> login(AuthenticationRequestDto dto) {
        Authentication authenticate =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String generateToken = jwtProvider.generateToken((org.springframework.security.core.userdetails.User) authenticate.getPrincipal());
        // TODO: 2/25/22 check if user login details match, if not handle it.

        AuthenticationResponse response = AuthenticationResponse.builder()
                .authenticationToken(generateToken)
                .refreshToken(refreshTokenService.generateRefreshToken().getRefreshToken())
                .username(dto.getUsername())
                .expirationData(Instant.now().plusMillis(18_000_000L))
                .build();
        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(()
                -> new IllegalArgumentException("Email not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getIsEnabled(),
                true,
                true,
                true,
                grantedAuthority("USER")
        );
    }

    private Collection<? extends GrantedAuthority> grantedAuthority(String user) {
        return Collections.singletonList(new SimpleGrantedAuthority(user));
    }
}
