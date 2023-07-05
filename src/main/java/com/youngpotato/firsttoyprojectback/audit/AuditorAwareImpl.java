package com.youngpotato.firsttoyprojectback.audit;

import com.youngpotato.firsttoyprojectback.utils.CommonUtil;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of("SYSTEM");
        }

        return Optional.of(CommonUtil.getLoginMemberEmail());
    }
}
