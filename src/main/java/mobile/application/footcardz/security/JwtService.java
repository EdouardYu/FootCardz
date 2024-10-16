package mobile.application.footcardz.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.application.footcardz.dto.user.RefreshTokenDTO;
import mobile.application.footcardz.dto.user.TokensDTO;
import mobile.application.footcardz.entity.Jwt;
import mobile.application.footcardz.entity.RefreshToken;
import mobile.application.footcardz.entity.User;
import mobile.application.footcardz.repository.JwtRepository;
import mobile.application.footcardz.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class JwtService {
    private final UserService userService;
    private final JwtRepository jwtRepository;

    @Value("${encryption.key}")
    private String ENCRYPTION_KEY;

    private final Map<Integer, Object> userLocks = new ConcurrentHashMap<>();

    public TokensDTO generate(String username) {
        User user = this.userService.loadUserByUsername(username);
        synchronized (this.getUserLock(user.getId())) {
            try {
                this.jwtRepository.disableTokensByUser(user.getUsername());

                long currentTime = System.currentTimeMillis();
                long expirationTime = currentTime + 4 * 60 * 60 * 1000; // 4 hours in milliseconds

                String bearer = this.generateJwt(user, currentTime, expirationTime);

                RefreshToken refreshToken = RefreshToken.builder()
                    .value(UUID.randomUUID().toString())
                    .expiredAt(Instant.ofEpochMilli(currentTime).plus(30, ChronoUnit.DAYS))
                    .build();


                Jwt jwt = Jwt.builder()
                    .value(bearer)
                    .expiredAt(Instant.ofEpochMilli(expirationTime))
                    .enabled(true)
                    .refreshToken(refreshToken)
                    .user(user)
                    .build();

                this.jwtRepository.save(jwt);

                return TokensDTO.builder()
                    .bearer(bearer)
                    .refresh(refreshToken.getValue())
                    .build();
            } finally {
                this.userLocks.remove(user.getId());
            }
        }
    }

    private Object getUserLock(Integer userId) {
        return this.userLocks.computeIfAbsent(userId, key -> new Object());
    }

    private String generateJwt(User user, long currentTime, long expirationTime) {
        return Jwts.builder()
            .issuedAt(new Date(currentTime))
            .expiration(new Date(expirationTime))
            .subject(String.valueOf(user.getId()))
            .claim("role", user.getRole().name())
            .signWith(this.getKey())
            .compact();
    }

    public Jwt findTokenByValue(String token) {
        return this.jwtRepository.findByValueAndEnabled(token, true)
            .orElseThrow(() -> new SignatureException("Invalid token"));
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = this.getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    public int extractId(String token) {
        return this.getClaim(token, claims -> Integer.parseInt(claims.getSubject()));
    }

    private Date getExpirationDateFromToken(String token) {
        return this.getClaim(token, Claims::getExpiration);
    }

    private <T> T getClaim(String token, Function<Claims, T> function) {
        Claims claims = this.getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getKey() {
        byte[] decoder = Decoders.BASE64.decode(this.ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }

    public void signOut() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Jwt jwt = this.jwtRepository.findUserTokenByValidity(user.getUsername(), true)
            .orElseThrow(() -> new SignatureException("Tokens are already disabled"));

        jwt.setEnabled(false);
        this.jwtRepository.save(jwt);
    }

    public TokensDTO refreshToken(RefreshTokenDTO refreshToken) {
        Jwt jwt = this.jwtRepository.findByEnabledRefreshToken(true, Instant.now(), refreshToken.getRefresh())
            .orElseThrow(() -> new SignatureException("Invalid or disabled refresh token"));

        return this.generate(jwt.getUser().getEmail());
    }

    @Scheduled(cron = "@daily")
    public void removeUselessTokens() {
        log.info("Deletion of useless tokens at: {}", Instant.now());
        this.jwtRepository.deleteAllByEnabledOrRefreshTokenExpiredAtBefore(false, Instant.now());
    }
}
