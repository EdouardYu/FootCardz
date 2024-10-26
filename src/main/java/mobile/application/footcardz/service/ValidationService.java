package mobile.application.footcardz.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.application.footcardz.entity.user.User;
import mobile.application.footcardz.entity.user.Validation;
import mobile.application.footcardz.repository.ValidationRepository;
import mobile.application.footcardz.service.exception.ValidationCodeException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Transactional
@AllArgsConstructor
@Service
public class ValidationService {
    private final ValidationRepository validationRepository;
    private final NotificationService notificationService;
    private final int VALIDITY_DURATION = 10;
    private final String CHRONO_UNIT = "minutes";

    private final Map<Integer, Object> userLocks = new ConcurrentHashMap<>();

    public void register(User user) {
        synchronized (this.getUserLock(user.getId())) {
            try {
                this.validationRepository.disableValidationCodesByUser(user.getUsername());
                Validation validation = generateValidationCode(user);
                this.notificationService.sendActivationCodeEmail(validation, this.VALIDITY_DURATION, this.CHRONO_UNIT);
            } finally {
                this.userLocks.remove(user.getId());
            }
        }
    }

    public void resetPassword(User user) {
        synchronized (this.getUserLock(user.getId())) {
            try {
                this.validationRepository.disableValidationCodesByUser(user.getUsername());
                Validation validation = generateValidationCode(user);
                this.notificationService.sendPasswordResetEmail(validation, this.VALIDITY_DURATION, this.CHRONO_UNIT);
            } finally {
                this.userLocks.remove(user.getId());
            }
        }
    }

    private Object getUserLock(Integer userId) {
        return this.userLocks.computeIfAbsent(userId, key -> new Object());
    }

    private Validation generateValidationCode(User user) {
        Random random = new Random();
        int randomInteger = random.nextInt(1_000_000);
        String code = String.format("%06d", randomInteger);

        Validation validation = Validation.builder()
            .code(code)
            .expiredAt(Instant.now().plus(this.VALIDITY_DURATION, ChronoUnit.valueOf(this.CHRONO_UNIT.toUpperCase())))
            .enabled(true)
            .user(user)
            .build();

        return this.validationRepository.save(validation);
    }

    public Validation findUserActivationCode(String email, String activationCode) {
        return this.validationRepository.findUserValidationCode(email, activationCode)
            .orElseThrow(() -> new ValidationCodeException("Invalid activation code"));
    }

    public Validation findUserPasswordResetCode(String email, String passwordResetCode) {
        return this.validationRepository.findUserValidationCode(email, passwordResetCode)
            .orElseThrow(() -> new ValidationCodeException("Invalid password reset code"));
    }

    @Scheduled(cron = "@daily")
    public void removeUselessValidationCodes() {
        log.info("Deletion of expired validation codes at: {}", Instant.now());
        this.validationRepository.deleteAllByEnabledOrExpiredAtBefore(
            false,
            Instant.now().minus(1, ChronoUnit.DAYS) // 1 day after validation code expires
        );
    }
}
