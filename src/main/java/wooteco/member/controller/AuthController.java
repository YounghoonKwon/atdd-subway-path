package wooteco.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.member.service.AuthService;
import wooteco.member.controller.dto.request.TokenRequestDto;
import wooteco.member.controller.dto.response.TokenResponseDto;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login/token")
    public ResponseEntity<TokenResponseDto> loginWithToken(@RequestBody TokenRequestDto tokenRequestDto) {
        TokenResponseDto tokenResponse = authService.createToken(tokenRequestDto);
        return ResponseEntity.ok().body(tokenResponse);
    }
}