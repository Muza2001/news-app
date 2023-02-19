package digital.one.service;

import digital.one.dto.request.AuthenticationRequest;
import digital.one.dto.request.UserEditPassword;
import digital.one.dto.request.UserEditRequest;
import digital.one.dto.request.UserRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<?> register(UserRequest dto);

    ResponseEntity<?> login(AuthenticationRequest dto);

    ResponseEntity<?> findById(Long id);

    ResponseEntity<?> edit(Long id, UserEditRequest request);

    ResponseEntity<?> editPassword(Long id, UserEditPassword userEditPassword);

    ResponseEntity<?> delete(Long id);
}
