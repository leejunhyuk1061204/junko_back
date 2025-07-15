package kr.co.junko.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class Jwt {
private static SecretKey priKey = null;
	
	public static SecretKey getPriKey() {
		return priKey;
	}

	public static void setPriKey() {
		Jwt.priKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	}

	public static String setToken(Map<String, Object> map) {
		return Jwts.builder()
				.setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT")
				.setClaims(map)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis()+(1000*60*60*8)))
				.signWith(priKey).compact();
	}
		
	public static String setToken(String key, Object value) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, value);
		return setToken(map);
	}
	
	public static Map<String, Object> readToken(String token) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(priKey).build()
					.parseClaimsJws(token).getBody();
			for (String key : claims.keySet()) {
				result.put(key, claims.get(key));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("id", "");
		}
		return result;
	}

	public static int getUserIdx(HttpServletRequest request) {
	    String authHeader = request.getHeader("Authorization");
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        String token = authHeader.substring(7);
	        try {
	            Claims claims = Jwts.parserBuilder()
	                    .setSigningKey(priKey) 
	                    .build()
	                    .parseClaimsJws(token)
	                    .getBody();

	            return (int) claims.get("user_idx");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return 0;
	}

}
