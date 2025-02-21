package me.carmelo.theforums.service.auth;

public interface ISessionService {

    boolean isSessionActive(String username);
    void createSession(String username);
    boolean invalidateSession(String username);

}
