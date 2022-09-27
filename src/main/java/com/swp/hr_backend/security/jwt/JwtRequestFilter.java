package com.swp.hr_backend.security.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.repository.AccountRepository;
import com.swp.hr_backend.utils.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,  HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/refresh-token")) {
            filterChain.doFilter(request, response);
        } else {
            // JWT Token is in the form "Bearer token". Remove Bearer word and get
            // only the Token
            final String requestTokenHeader = request.getHeader("Authorization");
            String username = null;
            String accessToken = null;
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                accessToken = requestTokenHeader.substring(7).trim();
                try {
                    username = jwtTokenUtil.getUsernameFromToken(accessToken);
                } catch (SignatureException e) {
                    log.error("Invalid jwt signature", e.getMessage());
                } catch (MalformedJwtException e) {
                    log.error("Invalid JWT Token", e.getMessage());
                } catch (IllegalArgumentException e) {
                    log.error("Unable to get JWT Token", e.getMessage());
                } catch (ExpiredJwtException e) {
                    log.error("jwt has expired", e.getMessage());
                    CustomError customError = CustomError.builder().code("token.expired").field("accessToken")
                            .message("JWT Token has expired").build();
                    responseToClient(response, customError, HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } else {
                logger.warn("JWT Token does not begin with Bearer String");
            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional<Account> accountOptional = accountRepository.findByUsername(username);
                if (accountOptional.isPresent()) {
                    Account account = accountOptional.get();
                    if (jwtTokenUtil.validateToken(accessToken, account)) {
                        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                                account.getUsername(), account.getPassword(), authorities);
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            }
            filterChain.doFilter(request, response);

        }

    }

    private void responseToClient(HttpServletResponse response, CustomError customError, int httpStatus)
            throws IOException {
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        response.setStatus(httpStatus);
        Map<String, CustomError> map = new HashMap<>();
        map.put("error", customError);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        response.getOutputStream().print(mapper.writeValueAsString(map));
        response.flushBuffer();
    }

}
