package main.models.users;

import lombok.Data;

@Data
public class UserSession {

    private String sessionId;
    private String subCode;
    private String context;
    private int currentPage;
    private String lastResponse;

//    public UserSession(String sessionId, String s, int i, String s1) {
//    }
}

