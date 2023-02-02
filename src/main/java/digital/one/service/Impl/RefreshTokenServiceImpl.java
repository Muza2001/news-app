package digital.one.service.Impl;

import digital.one.dto.RefreshTokenRequest;
import digital.one.model.auth.RefreshToken;
import digital.one.repository.RefreshTokenRepository;
import digital.one.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken generateRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        refreshToken.setCreatedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    public void validationToken(String refreshToken) {
        refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() ->
                        new IllegalArgumentException("Refresh token invalid"));
    }

    @Override
    public void refreshTokenDelete(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(request.
                getToken()).orElseThrow(() ->
                new IllegalArgumentException("Token not found"));
        refreshTokenRepository.delete(refreshToken);
    }
}
