package com.example.demo.JWT;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    @Autowired
    public JWTRequestFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {

                String email = jwtUtil.extractEmail(token);

                // if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //     UserDetails userDetails = User.withUsername(email)
                //                                 .password("")
                //                                 .roles("USER")
                //                                 .build();

                //     if (jwtUtil.validateToken(token, email)) {
                //         UsernamePasswordAuthenticationToken authenticationToken =
                //             new UsernamePasswordAuthenticationToken(
                //                 userDetails, null, userDetails.getAuthorities()
                //             );
                //         SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                //     }
                    
                // }


                if (email != null && jwtUtil.validateToken(token, email)) {
                    // Assign ROLE_USER by default for valid tokens
                    UserDetails userDetails = User.withUsername(email)
                                                .password("")
                                                .roles("USER")  // simpler
                                                .build();

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                            );

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }


            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                return;
            } catch (io.jsonwebtoken.JwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401

                return;
            }
        }

        chain.doFilter(request, response);
    }

}
