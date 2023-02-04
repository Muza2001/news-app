package digital.one.service;

import digital.one.dto.request.RefreshTokenRequest;
import digital.one.model.auth.RefreshToken;

public interface RefreshTokenService {

    RefreshToken generateRefreshToken();

    void validationToken(String refreshToken);

    void refreshTokenDelete(RefreshTokenRequest request);

}
