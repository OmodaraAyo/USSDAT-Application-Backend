package main.utils;

import main.models.users.UserSession;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionStore {
    private final ConcurrentHashMap<String, UserSession> sessionMap = new ConcurrentHashMap<>();


    public UserSession getSession(String sessionId) {
        return sessionMap.getOrDefault(sessionId, new UserSession(sessionId, "", 1, ""));
    }


    public void saveSession(UserSession session) {
        sessionMap.put(session.getSessionId(), session);
    }


    public void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
    }
}