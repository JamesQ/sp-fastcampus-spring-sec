package com.sp.fc.web.controller;

import com.sp.fc.user.domain.SpUser;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;

@Controller
public class SessionController {

    private final SessionRegistry sessionRepository;

    public SessionController(SessionRegistry sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @GetMapping("/sessions")
    public String sessions(Model model) {
        model.addAttribute("sessionList",
                sessionRepository.getAllPrincipals().stream().map(p -> UserSession.builder()
                        .username(((SpUser) p).getUsername())
                        .sessions(sessionRepository.getAllSessions(p, false).stream().map(s ->
                                SessionInfo.builder()
                                        .sessionId(s.getSessionId())
                                        .time(s.getLastRequest())
                                        .build())
                                .collect(Collectors.toList()))
                        .build()).collect(Collectors.toList())
        );
        return "/sessionList";
    }

    @PostMapping("/session/expire")
    public String expireSession(@RequestParam String sessionId) {
        SessionInformation sessionInformation = sessionRepository.getSessionInformation(sessionId);
        if(!sessionInformation.isExpired()) {
            sessionInformation.expireNow();
        }
        return "redirect:/sessions";
    }

    @GetMapping("/session-expired")
    public String sessionExpired() {
        return "/sessionExpired";
    }

}
