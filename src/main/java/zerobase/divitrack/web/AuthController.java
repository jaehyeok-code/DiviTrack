package zerobase.divitrack.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.divitrack.model.Auth;
import zerobase.divitrack.model.MemberEntity;
import zerobase.divitrack.security.TokenProvider;
import zerobase.divitrack.service.MemberService;

@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final MemberService memberService;

    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request){
        // 회원가입을 위한 API
        MemberEntity result = this.memberService.register(request);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request){
        // 로그인용 API
        var member = this.memberService.authenticate(request);
        var token = this.tokenProvider.generateToken(member.getUsername(),member.getRoles());
        return ResponseEntity.ok(token);
    }



}
