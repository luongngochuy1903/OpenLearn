package com.example.online.config;

import com.example.online.authentication.handler.OAuth2AuthenticationSuccessHandler;
import com.example.online.document.factory.DocumentGenerateFactory;
import com.example.online.document.service.DocumentService;
import com.example.online.domain.model.User;
import com.example.online.enumerate.DocumentOf;
import com.example.online.enumerate.LoginType;
import com.example.online.enumerate.Role;
import com.example.online.enumerate.UploadType;
import com.example.online.exception.BadRequestException;
import com.example.online.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final UserRepository userRepository;
    private final AuthenticationProvider authenticationProvider;
    private final DocumentGenerateFactory documentGenerateFactory;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/auth/**", "/v3/api-docs/**",
                        "/swagger-ui/**", "/swagger-ui.html",
                        "/oauth2/**", "/login/oauth2/**")
                        .permitAll().anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //http://localhost:8080/oauth2/authorization/google
                .oauth2Login(oath2 ->
                        oath2.userInfoEndpoint(userinfo -> userinfo.userService(oauth2UserService()))
                                .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return request -> {
            OAuth2User oauth2User = delegate.loadUser(request);

            // TODO: Custom Logic Here
            // 1. Get registrationId (google, github, etc.)
            String registrationId = request.getClientRegistration().getRegistrationId();
            // 2. Get user attributes
            java.util.Map<String, Object> attributes = oauth2User.getAttributes();
            // 3. Determine unique identifier (e.g., email, id from provider)
            String userIdentifier = null;
            String email = null;
            if ("google".equals(registrationId)) {
                userIdentifier = (String) attributes.get("sub"); // Google uses email
                email = (String) attributes.get("email");
                // Add logic for other providers

                if (userIdentifier == null) {
                    throw new OAuth2AuthenticationException("Cannot determine user identifier from OAuth2 provider: " + registrationId);
                }

                User user = userRepository.findBySub(userIdentifier)
                        .orElse(null);

                // 4. Handle user with email domain changed, but it is still him/her
                if (user != null && email != null && !email.equals(user.getEmail())){
                    if (userRepository.existsByEmailAndIdNot(email, user.getId())) {
                        throw new OAuth2AuthenticationException(
                                new OAuth2Error("email_exists"),
                                "This email has already been exists."
                        );
                    }
                    user.setEmail(email);
                    userRepository.save(user);
                }

                // 5. If user does not exist, create new user record
                if (user == null) {
                    if (userRepository.existsByEmail(email)) {
                        throw new OAuth2AuthenticationException(
                                new OAuth2Error("email_exists"),
                                "This email has already been exists. Try another account or using default login."
                        );
                    }
                    user = User.builder()
                            .firstName((String) attributes.get("given_name"))
                            .lastName((String) attributes.get("family_name"))
                            .email((String) attributes.get("email"))
                            .sub(userIdentifier)
                            .role(Role.USER)
                            .loginType(LoginType.GOOGLE)
                            .build();
                    userRepository.save(user);

                    DocumentService documentService = documentGenerateFactory.getService(DocumentOf.USER);
                    documentService.createDocument(user, UploadType.IMAGE, "default_avatar_url");
                }

                // 8. Return a new OAuth2User or a custom implementation
                // We wrap the original user with potentially updated authorities and a new name attribute key if needed
                String userNameAttributeName = request.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
                if (userNameAttributeName == null) {
                    userNameAttributeName = "sub"; // Or "login" for GitHub
                }

                // Constructor takes authorities, attributes, and nameAttributeKey
                return new DefaultOAuth2User(
                        user.getAuthorities(),
                        attributes,
                        userNameAttributeName
                );
            } else {
                throw new BadRequestException("Something happened !");
            }
        };
    }
}
