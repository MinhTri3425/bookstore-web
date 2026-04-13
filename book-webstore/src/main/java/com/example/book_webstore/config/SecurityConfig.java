package com.example.book_webstore.config;

import com.example.book_webstore.model.User;
import com.example.book_webstore.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(authz -> authz
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
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/shipper/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/cart/**", "/order/**", "/my-orders/**").authenticated()
                        .requestMatchers("/profile/**").authenticated()
                        // Tất cả các request khác phải đăng nhập
                        .anyRequest().authenticated())

                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            // Lấy thông tin User từ DB
                            String email = authentication.getName();
                            User user = userRepository.findByEmail(email);

                            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                            boolean isAdmin = authorities.stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                            // LOGIC: ĐĂNG NHẬP XONG LÀ VÀO DASHBOARD NGAY
                            if (isAdmin) {
                                // Admin vào Dashboard quản trị
                                response.sendRedirect("/admin/dashboard");
                            } else if (user != null && user.isShipper()) {
                                // Shipper vào Dashboard giao hàng
                                response.sendRedirect("/shipper/dashboard");
                            } else {

                                response.sendRedirect("/books");
                            }
                        })
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}