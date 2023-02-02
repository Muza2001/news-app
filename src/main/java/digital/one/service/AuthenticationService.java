package digital.one.service;

import digital.one.dto.AuthenticationRequestDto;
import digital.one.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<?> register(UserDto dto);

    ResponseEntity<?> login(AuthenticationRequestDto dto);
}
