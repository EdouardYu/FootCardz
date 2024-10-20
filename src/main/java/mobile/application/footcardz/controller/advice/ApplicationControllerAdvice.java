package mobile.application.footcardz.controller.advice;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import mobile.application.footcardz.dto.ErrorEntity;
import mobile.application.footcardz.service.exception.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ApplicationControllerAdvice {
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
        AlreadyUsedException.class,
        ValidationCodeException.class,
        UsernameNotFoundException.class,
        NotYetEnabledException.class,
        LockedException.class,
        BadPasswordException.class,
        PlayerException.class,
        TeamException.class,
        LeagueException.class,
        NationalityException.class,
        IllegalArgumentException.class,
        NumberFormatException.class
    })
    public @ResponseBody ErrorEntity handleBadRequestException(RuntimeException e) {
        log.warn(String.valueOf(e));
        return new ErrorEntity(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorEntity handleValidationExceptions(MethodArgumentNotValidException e) {
        log.warn(String.valueOf(e));
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .toList()
            .stream().findFirst()
            .orElse(e.getMessage());

        return new ErrorEntity(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadCredentialsException.class})
    public @ResponseBody ErrorEntity handleBadCredentialsException(BadCredentialsException e) {
        log.warn(String.valueOf(e));
        return new ErrorEntity(HttpStatus.BAD_REQUEST.value(), "Invalid username or password");
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public @ResponseBody ErrorEntity handleBadRequestException(HttpMessageNotReadableException e) {
        log.warn(String.valueOf(e));
        return new ErrorEntity(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid request format: " + e.getMessage()
        );
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public @ResponseBody ErrorEntity handleBadRequestException(HttpMediaTypeNotSupportedException e) {
        log.warn(String.valueOf(e));
        return new ErrorEntity(
            HttpStatus.BAD_REQUEST.value(),
            e.getMessage()
        );
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MissingServletRequestPartException.class})
    public @ResponseBody ErrorEntity handleBadRequestException(MissingServletRequestPartException e) {
        log.warn(String.valueOf(e));
        String message = e.getMessage().substring(0, e.getMessage().length() - 1);
        return new ErrorEntity(
            HttpStatus.BAD_REQUEST.value(),
            message
        );
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class})
    public @ResponseBody ErrorEntity handleBadRequestException(ConstraintViolationException e) {
        log.warn(String.valueOf(e));
        String message = e.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));

        return new ErrorEntity(
            HttpStatus.BAD_REQUEST.value(),
            message
        );
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler({
        AlreadyProcessedException.class,
        DailyPlayerAlreadyAssignedException.class
    })
    public @ResponseBody ErrorEntity handleAlreadyProcessedException(RuntimeException e) {
        log.warn(String.valueOf(e));
        return new ErrorEntity(HttpStatus.CONFLICT.value(), e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
        SignatureException.class,
        MalformedJwtException.class,
        ExpiredJwtException.class
    })
    public @ResponseBody ErrorEntity handleSignatureExceptionException(RuntimeException e) {
        log.warn(String.valueOf(e));
        return new ErrorEntity(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler({AccessDeniedException.class})
    public @ResponseBody ErrorEntity handleAccessDeniedException(AccessDeniedException e) {
        log.warn(String.valueOf(e));
        return new ErrorEntity(HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({RuntimeException.class})
    public @ResponseBody ErrorEntity handleRuntimeException(RuntimeException e) {
        log.error(String.valueOf(e));
        return new ErrorEntity(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");
    }
}
