package digital.one.controller;

import digital.one.dto.AuthenticationRequestDto;
import digital.one.dto.UserDto;
import digital.one.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto dto){
        return ResponseEntity.status(201).body(service.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestDto dto){
        return ResponseEntity.status(200).body(service.login(dto));
    }
}
