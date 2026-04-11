// package com.example.book_webstore.service.strategy;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.example.book_webstore.service.strategy.impl.FacebookLoginImpl;
// import com.example.book_webstore.service.strategy.impl.GoogleLoginImpl;
// //import com.example.book_webstore.service.strategy.impl.GithubMockStrategy;
// @Service
// public class LoginFactory {
//     @Autowired
//     private GoogleLoginImpl googleStrategy;
    
//     @Autowired
//     private FacebookLoginImpl facebookStrategy;

//     // @Autowired
//     // private GithubMockStrategy githubStrategy;

//     public LoginStrategy getStrategy(String provider) {
//         if ("google".equalsIgnoreCase(provider)) {
//             return googleStrategy;
//         } else if ("facebook".equalsIgnoreCase(provider)) {
//             return facebookStrategy;
//         } 
//         // else if ("github".equalsIgnoreCase(provider)) {
//         //     return githubStrategy;
//         // }
//         throw new IllegalArgumentException("Không hỗ trợ phương thức đăng nhập: " + provider);
//     }
// }


