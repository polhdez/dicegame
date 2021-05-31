package com.polhdez.dicegame.controller;

import com.polhdez.dicegame.model.User;
import com.polhdez.dicegame.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
public class UserApiController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("checktoken")
    public ResponseEntity checkToken(@RequestHeader("Authorization") String token) {
        return new ResponseEntity(true, HttpStatus.OK);
    }

    @GetMapping("loggedUser")
    public ResponseEntity getLoggedUser(@RequestHeader("Authorization") String token) {
        return new ResponseEntity(userRepository.findByToken(token), HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity loginPost(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        try {
            // Get user from database and check if exists
            User user = userRepository.findByUsername(username);
            if (user == null)
                // I return forbidden for security so people dont know which users exist
                return new ResponseEntity(null, HttpStatus.FORBIDDEN);

            // Compare given password to the hashed one
            if (BCrypt.checkpw(password, user.getPasswordHash())) {

                // Create new token and return it if matches
                user.setToken(getJWTToken(username));
                userRepository.save(user);
                return new ResponseEntity(user.getToken(), HttpStatus.OK);
            }
            else
                return new ResponseEntity(null, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @PostMapping("register")
    public ResponseEntity registerPost(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        try {
            // Check if username is taken
            if (userRepository.findByUsername(username) != null || username.isBlank())
                return new ResponseEntity(null, HttpStatus.FORBIDDEN);

            // If not, hash the password string with bcrypt, create token and add user to database
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
            User user = new User(username, passwordHash);
            userRepository.save(user);
            return new ResponseEntity("User created!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("modify")
    public ResponseEntity modifyPlayer(@RequestHeader("Authorization") String token, @RequestParam("newName") String newName) {
        try {
            User user = userRepository.findByToken(token);
            if (user == null)
                return new ResponseEntity(null, HttpStatus.NOT_MODIFIED);
            user.setUsername(newName);
            return new ResponseEntity(userRepository.save(user), HttpStatus.OK);
        }
        catch(Exception e)  {
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Method from https://blog.softtek.com/en/token-based-api-authentication-with-spring-and-jwt
    private String getJWTToken(String username) {
        String secretKey = "veryDifficultKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId("myId")
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512,
                        secretKey.getBytes()).compact();

        return "Bearer " + token;
    }
}
