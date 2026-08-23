package com.Orka.Filter;

import com.Orka.constant.Constant;
import com.Orka.user.User;
import com.Orka.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;


    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie [] cookies = request.getCookies();

        if(cookies == null){
            filterChain.doFilter(request,response);
            log.info("Cookies not found");
            return;
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = "";
        for (Cookie cookie : cookies){
            if(cookie.getName().equals(Constant.JWT_COOKIE)){
                try{
                    jwt = cookie.getValue();
                    String username = jwtUtil.getUsername(jwt);
                    String email = jwtUtil.getEmail(jwt);
                    User tempUser = User.builder().email(email).username(username).build();
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            tempUser,
                            null,
                            List.of()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (Exception e) {
                    log.error("fake jwt detected");
                }
                break;
            }
        }
        filterChain.doFilter(request,response);
    }
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil){
        return new JwtAuthenticationFilter(jwtUtil);
    }
}
