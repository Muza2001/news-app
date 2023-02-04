package digital.one.service;

import digital.one.dto.request.AuthenticationRequest;
import digital.one.dto.request.UserRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<?> register(UserRequest dto);

    ResponseEntity<?> login(AuthenticationRequest dto);
}
