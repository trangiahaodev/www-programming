package iuh.wwwprogramming.security;

import iuh.wwwprogramming.service.impl.ShopUserDetailsService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class ActiveAccountFilter extends OncePerRequestFilter {
    private final ShopUserDetailsService users;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof ShopPrincipal principal && !users.isCurrent(principal)) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            response.sendRedirect(request.getContextPath() + "/login?expired");
            return;
        }
        chain.doFilter(request, response);
    }
}
