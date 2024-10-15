package mobile.application.footcardz.service;

import lombok.AllArgsConstructor;
import mobile.application.footcardz.entity.Validation;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AllArgsConstructor
@Service
public class NotificationService {
    private final JavaMailSender javaMailSender;
    private final String EMAIL = "no-reply@footcardz.com";

    public void sendActivationCodeEmail(Validation validation, int validityDuration, String chronoUnit) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.EMAIL);
        message.setTo(validation.getUser().getEmail());
        message.setSubject("FootCardz - Activation Code");

        String text = "Here is the activation code to create your FootCardz account\n"
            + validation.getCode()
            + "\nThis code is only valid for " + validityDuration + " " + chronoUnit.toLowerCase();
        message.setText(text);

        this.javaMailSender.send(message);
    }

    public void sendPasswordResetEmail(Validation validation, int validityDuration, String chronoUnit) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.EMAIL);
        message.setTo(validation.getUser().getEmail());
        message.setSubject("FootCardz - Password Reset Code");

        String text = "Here is the code to reset your FootCardz account password\n"
            + validation.getCode()
            + "\nThis code is only valid for " + validityDuration + " " + chronoUnit.toLowerCase();
        message.setText(text);

        this.javaMailSender.send(message);
    }
}
