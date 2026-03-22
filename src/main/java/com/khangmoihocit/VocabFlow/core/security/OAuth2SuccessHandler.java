package com.khangmoihocit.VocabFlow.core.security;

import com.khangmoihocit.VocabFlow.modules.user.entities.User;
import com.khangmoihocit.VocabFlow.modules.user.repositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    UserRepository userRepository;
    JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        //Trích xuất thông tin từ Google
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        String googleId = oAuth2User.getAttribute("sub");

        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();

            if (user.getProvider().equals("LOCAL")) {
                user.setProvider("GOOGLE");
                user.setProviderId(googleId);
                userRepository.save(user);
            }

            if (user.getAvatarUrl() == null || user.getAvatarUrl().isEmpty()) {
                user.setAvatarUrl(picture);
                userRepository.save(user);
            }

        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(name);
            newUser.setAvatarUrl(picture);
            newUser.setRole("USER");
            newUser.setPasswordHash("");
            newUser.setProvider("GOOGLE");
            newUser.setProviderId(googleId);

            user = userRepository.save(newUser);
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(2 * 60 * 60); // 2h

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        getRedirectStrategy().sendRedirect(request, response, "http://localhost:5173/");
    }
}
