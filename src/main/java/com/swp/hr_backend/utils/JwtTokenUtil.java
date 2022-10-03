
package com.swp.hr_backend.utils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.swp.hr_backend.service.AccountService;
import com.swp.hr_backend.service.EmployeeService;
import com.swp.hr_backend.service.RoleService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.model.TokenPayLoad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenUtil implements Serializable {
	private static final long serialVersionUID = -2550185165626007488L;
	public static final long ACCESS_TOKEN_EXPIRED = 2 * 60 * 60; // 2 giờ
	public static final long REFRESH_TOKEN_EXPIRED = 2 * 24 * 60 * 60; // 2 ngày
	private final String secret = "HR_SECRET";
	private final String ISSUER = "HR_TEAM";
	private final AccountService accountService;
	private final EmployeeService employeeService;
	private final RoleService roleService;

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public TokenPayLoad getTokenPayLoad(String token) {
		return getClaimFromToken(token, (Claims claim) -> {
			Map<String, Object> mapResult = (Map<String, Object>) claim.get("payload");
			return TokenPayLoad.builder().roleName((String) mapResult.get("roleName")).build();
		});
	}

	private boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	private String doGenerateToken(Map<String, Object> claims, String subject, long expiredDate) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuer(ISSUER)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiredDate * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	public String generateToken(String username, long expiredDate, String rolename) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("payload", TokenPayLoad.builder().roleName(rolename).build());
		return doGenerateToken(claims, username, expiredDate);
	}

	public boolean validateToken(String token, Account account) {
		final String username = getUsernameFromToken(token);
		return (username.equals(account.getUsername()) && account.isStatus() && !isTokenExpired(token));
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
			return true;
		} catch (SignatureException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}

	public Account loggedAccount() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			String username = ((UserDetails) principal).getUsername();
			Account account = accountService.findAccountByUsername(username);
			return account;
		}
		return null;
	}

	public String getRoleNameByAccountId(String accountId) {
		try {
			Integer roleId = employeeService.findRoleIDByAccountID(accountId);
			String roleName = "";
			if (roleId != null) {
				roleName = roleService.findRolenameByRoleID(roleId).get();
			} else {
				roleName = AccountRole.CANDIDATE.toString();
			}
			return roleName;
		} catch (Exception e) {

		}
		return null;
	}
	
	public boolean checkPermissionCurrentAccount(AccountRole accRole) {
		Account acc = loggedAccount();
		if (acc == null || accRole == null)
			return false;
		return checkPermissionAccount(acc, accRole);
	}

	public boolean checkPermissionAccount(Account acc, AccountRole accRole) {
		if (acc == null || accRole == null)
			return false;
		if (accRole.equals(AccountRole.CANDIDATE)) {
			Candidate candidate = candRepo.findById(acc.getAccountID()).get();
			if(candidate != null) return true;
		} else {
			String roleName = getRoleNameByAccountId(acc.getAccountID());
			if (roleName == null)
				return false;
			if (roleName.equals(accRole.toString()))
				return true;
		}
		return false;
	}
}
