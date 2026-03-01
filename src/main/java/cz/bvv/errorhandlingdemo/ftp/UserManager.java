package cz.bvv.errorhandlingdemo.ftp;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Minimal in-memory FTP user manager used by embedded FTP server configuration.
 *
 * <p>This implementation is intentionally lightweight and exists only for local demo/test runtime.
 */
public final class UserManager implements org.apache.ftpserver.ftplet.UserManager {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    private void put(User user) {
        users.put(user.getName(), user);
    }

    @Override
    public void save(User user) {
        put(user);
    }

    /**
     * Local helper variant used by configuration code to avoid checked-exception signatures.
     *
     * @param user FTP user to persist in the in-memory store
     */
    public void saveLocal(User user) {
        put(user);
    }

    @Override public User getUserByName(String username) { return users.get(username); }

    @Override public String[] getAllUserNames() { return users.keySet().toArray(new String[0]); }

    @Override public void delete(String username) { users.remove(username); }

    @Override public boolean doesExist(String username) { return users.containsKey(username); }

    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        if (authentication instanceof UsernamePasswordAuthentication up) {
            User u = users.get(up.getUsername());
            if (u == null) throw new AuthenticationFailedException("No such user");
            if (!u.getPassword().equals(up.getPassword())) throw new AuthenticationFailedException("Bad credentials");
            return u;
        }
        throw new AuthenticationFailedException("Unsupported auth type: " + authentication.getClass().getName());
    }

    @Override public String getAdminName() { return "admin"; }

    @Override public boolean isAdmin(String username) { return false; }
}
