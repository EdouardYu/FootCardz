package mobile.application.footcardz.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mobile.application.footcardz.dto.player.PlayerDTO;
import mobile.application.footcardz.dto.user.*;
import mobile.application.footcardz.security.JwtService;
import mobile.application.footcardz.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(path = "signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void signUp(@Valid @RequestBody RegistrationDTO userDTO) {
        this.userService.signUp(userDTO);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "activate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void activate(@RequestBody ActivationDTO activationDTO) {
        this.userService.activate(activationDTO);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "activate/new", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void newActivationCode(@RequestBody EmailDTO userDTO) {
        this.userService.newActivationCode(userDTO);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(
        path = "signin",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TokensDTO signIn(@RequestBody AuthenticationDTO authenticationDTO) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            authenticationDTO.getUsername(),
            authenticationDTO.getPassword()
        ));

        return this.jwtService.generate(authenticationDTO.getUsername());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "signout")
    public void signOut() {
        this.jwtService.signOut();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "token/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody TokensDTO refreshToken(@RequestBody RefreshTokenDTO refreshToken) {
        return this.jwtService.refreshToken(refreshToken);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "password/reset", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void resetPassword(@RequestBody EmailDTO userDTO) {
        this.userService.resetPassword(userDTO);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "password/new", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void newPassword(@Valid @RequestBody PasswordResetDTO passwordResetDTO) {
        this.userService.newPassword(passwordResetDTO);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path = "profiles/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProfileDTO getProfile(@PathVariable Integer id) {
        return this.userService.getProfile(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping(
        path = "profiles/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ProfileDTO modifyProfile(@PathVariable Integer id, @Valid @RequestBody ProfileModificationDTO userDTO) {
        return this.userService.modifyProfile(id, userDTO);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping(path = "profiles/{id}/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void modifyPassword(@PathVariable Integer id, @Valid @RequestBody PasswordModificationDTO userDTO) {
        this.userService.modifyPassword(id, userDTO);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path = "users/{id}/players", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<PlayerDTO> getUserPlayers(@PathVariable Integer id, Pageable pageable) {
        return this.userService.getUserPlayers(id, pageable);
    }
}

