package org.example.t1_security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/premium")
public class PremiumController {

    @GetMapping("/features")
    @PreAuthorize("hasRole('PREMIUM_USER')")
    public String premiumFeatures() {
        return "Exclusive premium content";
    }
}

