package digital.one.config;

import digital.one.model.User;
import org.springframework.data.auditing.config.AuditingConfiguration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityAuditingConfig implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(authentication != null &&
                authentication.isAuthenticated() &&
                !authentication.getPrincipal()
                        .equals("anonymousUser")){
            User principal = (User) authentication.getPrincipal();
            return Optional.of(principal.getId());
        }
        return Optional.empty();
    }
}
