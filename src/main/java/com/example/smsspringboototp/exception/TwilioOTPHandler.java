package com.example.smsspringboototp.exception;
import com.example.smsspringboototp.dto.PasswordResetRequestDto;
import com.example.smsspringboototp.service.TwilioOtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class TwilioOTPHandler {
    @Autowired
    private TwilioOtpService service;

    public Mono<ServerResponse> sendOTP(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(PasswordResetRequestDto.class)
                .flatMap(dto -> service.sendOTPForPasswordReset(dto))
                .flatMap(dto -> ServerResponse.status(HttpStatus.OK)
                        .body(BodyInserters.fromValue(dto)))
                .onErrorResume(error -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue("Error sending OTP: " + error.getMessage()));
    }

    public Mono<ServerResponse> validateOTP(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(PasswordResetRequestDto.class)
                .flatMap(dto -> service.validateOTP(dto.getOneTimePassword(), dto.getUserName()))
                .flatMap(dto -> ServerResponse.status(HttpStatus.OK)
                        .bodyValue(dto))
                .onErrorResume(error -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue("Error validating OTP: " + error.getMessage()));
    }
}
