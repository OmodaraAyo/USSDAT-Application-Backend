package main.utils;

import main.models.users.UserSession;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionStore {
    private final ConcurrentHashMap<String, UserSession> sessionMap = new ConcurrentHashMap<>();

    public UserSession getSession(String sessionId) {
        UserSession userSession = sessionMap.get(sessionId);
        if (userSession == null) {
            userSession = new UserSession();
            userSession.setSessionId(sessionId);
            userSession.setSubCode("");
            userSession.setCurrentPage(1);
            userSession.setLastResponse("");
        }
        return userSession;
    }

    public void saveSession(UserSession session) {
        sessionMap.put(session.getSessionId(), session);
    }

    public void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
    }
}