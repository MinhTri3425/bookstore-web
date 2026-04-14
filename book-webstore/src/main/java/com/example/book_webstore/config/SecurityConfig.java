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
                .csrf(csrf -> csrf.disable()) // Lưu ý: Nếu dùng Production nên bật lại và
                // sửa JSP
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(authz -> authz
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()

                        // 1. TÀI NGUYÊN TĨNH & PUBLIC
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**", "/static/**", "/webjars/**")
                        .permitAll()
                        .requestMatchers("/", "/register", "/login", "/home", "/books/**", "/payment/vnpay-return", "/mock-login")
                        .permitAll()

                        // 2. PHÂN QUYỀN ADMIN (Chỉ Admin mới được vào)
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 3. PHÂN QUYỀN SHIPPER
                        // Vẫn để hasRole("USER") vì Shipper là một User đặc biệt,
                        // nhưng ta sẽ chặn thêm ở tầng Controller hoặc Interceptor bằng flag isShipper
                        .requestMatchers("/shipper/**").hasRole("USER")

                        // 4. CÁC TRANG CÒN LẠI YÊU CẦU LOGIN
                        .requestMatchers("/cart/**", "/order/**", "/my-orders/**", "/profile/**").authenticated()
                        .anyRequest().authenticated())

                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            // Logic chuyển hướng sau khi login (Giữ nguyên logic cũ của bạn là ổn)
                            var authorities = authentication.getAuthorities();
                            boolean isAdmin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                            String email = authentication.getName();
                            User user = userRepository.findByEmail(email);

                            if (isAdmin) {
                                response.sendRedirect("/admin/dashboard");
                            } else if (user != null && user.isShipper()) {
                                response.sendRedirect("/shipper/dashboard");
                            } else {
                                response.sendRedirect("/books");
                            }
                        })
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