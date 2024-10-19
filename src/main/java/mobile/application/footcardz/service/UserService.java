package mobile.application.footcardz.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.application.footcardz.dto.player.PlayerDTO;
import mobile.application.footcardz.dto.user.*;
import mobile.application.footcardz.entity.user.User;
import mobile.application.footcardz.entity.user.Validation;
import mobile.application.footcardz.entity.enumeration.Role;
import mobile.application.footcardz.repository.UserRepository;
import mobile.application.footcardz.service.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Transactional
@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    private final UserPlayerCollectionService userPlayerCollectionService;

    public void signUp(RegistrationDTO userDTO) {
        if(this.userRepository.existsByUsernameAndEnabled(userDTO.getUsername(), true))
            throw new AlreadyUsedException("Username already used");

        User user;
        Optional<User> dbUser= this.userRepository.findByEmail(userDTO.getEmail());
        if(dbUser.isPresent()) {
            user = dbUser.get();

            if(user.isEnabled())
                throw new AlreadyUsedException("Email already used");

            String encryptedPassword = this.passwordEncoder.encode(userDTO.getPassword());
            user.setUsername(userDTO.getUsername());
            user.setPassword(encryptedPassword);
        } else {
            String encryptedPassword = this.passwordEncoder.encode(userDTO.getPassword());
            user = User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(encryptedPassword)
                .enabled(false)
                .role(Role.USER)
                .build();
        }

        this.userRepository.save(user);
        this.validationService.register(user);
    }

    public void activate(ActivationDTO activationDTO) {
        Validation validation = this.validationService.findUserActivationCode(
            activationDTO.getEmail(),
            activationDTO.getCode()
        );

        if(Instant.now().isAfter(validation.getExpiredAt()))
            throw new ValidationCodeException("Expired activation code");

        if(!validation.isEnabled())
            throw new ValidationCodeException("Disabled activation code");

        User user = validation.getUser();
        if(user.isEnabled())
            throw new AlreadyProcessedException("User already enabled");

        user.setEnabled(true);
        this.userRepository.save(user);
    }

    public void newActivationCode(EmailDTO userDTO) {
        User user = this.userRepository.findByEmail(userDTO.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(user.isEnabled())
            throw new AlreadyProcessedException("User already enabled");

        this.validationService.register(user);
    }

    public void resetPassword(EmailDTO userDTO) {
        User user = this.userRepository.findByEmail(userDTO.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(!user.isEnabled())
            throw new NotYetEnabledException("User not yet enabled");

        this.validationService.resetPassword(user);
    }

    public void newPassword(PasswordResetDTO passwordResetDTO) {
        Validation validation = validationService.findUserPasswordResetCode(
            passwordResetDTO.getEmail(),
            passwordResetDTO.getCode()
        );

        User user = validation.getUser();
        if(!user.isEnabled())
            throw new NotYetEnabledException("User not yet enabled");

        if(Instant.now().isAfter(validation.getExpiredAt()))
            throw new ValidationCodeException("Expired password reset code");

        if(!validation.isEnabled())
            throw new ValidationCodeException("Disabled password reset code");

        String encryptedPassword = this.passwordEncoder.encode(passwordResetDTO.getPassword());
        user.setPassword(encryptedPassword);
        this.userRepository.save(user);
    }

    public ProfileDTO getProfile(Integer id) {
        User profile = this.userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ProfileDTO.builder()
            .username(profile.getUsername())
            .email(profile.getEmail())
            .role(profile.getRole())
            .build();
    }


    public ProfileDTO modifyProfile(Integer id, ProfileModificationDTO userDTO) {
        User user = hasPermission(id);

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        user = this.userRepository.save(user);

        return ProfileDTO.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole())
            .build();
    }

    public void modifyPassword(Integer id, PasswordModificationDTO userDTO) {
        User user = hasPermission(id);

        if (!passwordEncoder.matches(userDTO.getOldPassword(), user.getPassword())) {
            throw new BadPasswordException("Incorrect password");
        }

        String newEncryptedPassword = this.passwordEncoder.encode(userDTO.getNewPassword());

        user.setPassword(newEncryptedPassword);
        this.userRepository.save(user);
    }

    public Page<PlayerDTO> getUserPlayers(Integer id, Pageable pageable) {
        return this.userPlayerCollectionService.findAllByUser(id, pageable);
    }

    public Page<PlayerDTO> searchUserPlayers(Integer id, String term, Pageable pageable) {
        return this.userPlayerCollectionService.searchPlayersByUser(id, term, pageable);
    }

    private User hasPermission(Integer id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User dbUser = this.userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(dbUser.getId().equals(user.getId()) && Role.ADMINISTRATOR.equals(user.getRole()))
            throw new AccessDeniedException("Access denied");

        return dbUser;
    }

    @Override
    public User loadUserByUsername(String username) {
        return this.userRepository.findByUsernameOrEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User findById(Integer id) {
        return this.userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
