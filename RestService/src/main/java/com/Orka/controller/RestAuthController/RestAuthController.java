package com.Orka.controller.RestAuthController;

import com.Orka.apiContract.generated.ProtoHttpResponse;
import com.Orka.repository.UserRepository;
import com.Orka.user.User;
import com.Orka.util.JwtUtil;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.Duration;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class RestAuthController {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public RestAuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<ProtoHttpResponse> login(HttpServletRequest request, @RequestBody LoginRequestDTO loginRequestDTO, ServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user  = null;
        if(auth.getPrincipal() instanceof User){
            user = (User) auth.getPrincipal();
        }
        if(user == null){
//          username password login
            String email = loginRequestDTO.email;
            String password = loginRequestDTO.password;
            String username = loginRequestDTO.username;
            if(email==null && username==null){
                ProtoHttpResponse protoHttpResponse = ProtoHttpResponse.newBuilder().
                        setStatusCode(HttpStatus.BAD_REQUEST.value()).
                        setError("Both username and email cannot be empty").build();
                return ResponseEntity.badRequest().body(protoHttpResponse);

            }
                user = findUser(loginRequestDTO);
                if(user==null){
                    ProtoHttpResponse protoHttpResponse = ProtoHttpResponse.newBuilder().
                            setStatusCode(HttpStatus.NOT_FOUND.value()).
                            setError("user with email or username provided not found").build();
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(protoHttpResponse);
                }
                String HashedPassword = user.getPassword();
                boolean passwordCorrect = BCrypt.checkpw(password,HashedPassword);
                if(!passwordCorrect){
                    ProtoHttpResponse protoHttpResponse = ProtoHttpResponse.newBuilder().
                            setStatusCode(HttpStatus.UNAUTHORIZED.value()).
                            setError("wrong credentials").build();
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(protoHttpResponse);
                }
                return getJwtCookie(email,username);
        }
//        Oauth2 login it will set email and username both
        Optional<User> existingUser = userRepository.findByEmailAndUsername(user.getEmail(),user.getUsername());
        if(existingUser.isPresent()){
            user =  existingUser.get();
        }
//        if it is already present, then it is idempotent
//        if not it will store this user once and for all.
        userRepository.save(user);
        return getJwtCookie(user.getEmail(),user.getUsername());
    }
    private ResponseEntity<ProtoHttpResponse> getJwtCookie(String email, String username){
        String jwt = jwtUtil.getJwt(email,username);
        ResponseCookie jwtCookie = ResponseCookie.
                from("jwt",jwt).
                httpOnly(true)
                .sameSite("lax")
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();
        ProtoHttpResponse protoHttpResponse = ProtoHttpResponse.newBuilder()
                .setStatusCode(200)
                .setError("")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(protoHttpResponse);
    }

    private User findUser(LoginRequestDTO loginRequestDTO){
        if(loginRequestDTO.username!=null){
            return userRepository.findByUsername(loginRequestDTO.username).orElse(null);
        }else{
            return userRepository.findByEmail(loginRequestDTO.email).orElse(null);
        }
    }

    @PostMapping("/signup")
    private ResponseEntity<ProtoHttpResponse> S(String email, String username){
        return null;
    }
}
