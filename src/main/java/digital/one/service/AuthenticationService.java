package digital.one.service;

import digital.one.dto.request.*;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<?> register(UserRequest dto);

    ResponseEntity<?> login(AuthenticationRequest dto);

    ResponseEntity<?> findById(Long id);

    ResponseEntity<?> edit(UserEditRequest request);

    ResponseEntity<?> editPassword(UserEditPassword userEditPassword);

    ResponseEntity<?> delete(Long id);

    ResponseEntity<?> logout(RefreshTokenRequest request);

    ResponseEntity<?> getAll();

}
