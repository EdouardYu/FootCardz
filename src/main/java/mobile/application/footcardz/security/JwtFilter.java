package mobile.application.footcardz.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import mobile.application.footcardz.entity.user.Jwt;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@AllArgsConstructor
@Service
public class JwtFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
        @Nonnull HttpServletRequest request,
        @Nonnull HttpServletResponse response,
        @Nonnull FilterChain filterChain
    ) {
        String token;
        Jwt dbJwt = null;
        Integer id = null;
        boolean isTokenExpired = true;

        try {
            String authorization = request.getHeader("Authorization");

            if(authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
                dbJwt = this.jwtService.findTokenByValue(token);
                isTokenExpired = this.jwtService.isTokenExpired(token);
                id = this.jwtService.extractId(token);
            }

            if(!isTokenExpired
                && dbJwt.getUser().getId().equals(id)
                && SecurityContextHolder.getContext().getAuthentication() == null
            ) {
                UserDetails userDetails = dbJwt.getUser();
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            this.handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
