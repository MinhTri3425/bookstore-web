package com.example.book_webstore.config;

import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF để dễ làm việc với form (nếu cần)
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(authz -> authz
                        // Cho phép các request nội bộ của Spring
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()

                        // QUAN TRỌNG: Cho phép truy cập tài nguyên tĩnh và THƯ MỤC UPLOADS (ảnh sách)
                        .requestMatchers(
                                "/",
                                "/register",
                                "/login",
                                "/error",
                                "/mock-login",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**", // <--- PHẢI CÓ DÒNG NÀY ẢNH MỚI HIỆN
                                "/books/**", // Cho phép xem chi tiết sách không cần login
                                "/home")
                        .permitAll()

                        // Phân quyền cho Admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/profile/**").authenticated()
                        // Tất cả các request khác phải đăng nhập
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            // Logic chuyển hướng sau khi login thành công
                            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                            boolean isAdmin = authorities.stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                            if (isAdmin) {
                                response.sendRedirect("/admin/dashboard");
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Khởi tạo rõ ràng
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Ép kiểu nếu IDE bị "ngáo" (tuy nhiên thường không cần nếu import đúng)
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}
