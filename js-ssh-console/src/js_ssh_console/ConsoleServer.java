package js_ssh_console;

import java.io.IOException;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.*;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

/**
 * Console server class, responsible for firing up SSH service and connect each client with a {@link NashhornShell}
 */
public class ConsoleServer implements Factory<Command> {
    
    private SshServer _sshd;

    public ConsoleServer() throws IOException {
        
        // Setup the SSH server on port 8022 with a trivial password authentication
        _sshd = SshServer.setUpDefaultServer();
        _sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
            public boolean authenticate(String name, String pwd, ServerSession s) {
                return "admin".equals(name) && "geheim".equals(pwd);
            }
        });
        _sshd.setPort(8022);
        
        // Creates an encryption key for SSH communcation stored in the specified file
        _sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("rconsole.ser"));
        
        // This makes the SSHD server call method create() on each shell connection
        _sshd.setShellFactory(this);
        _sshd.start();
    }
    
    public void shutdown() throws InterruptedException {
        _sshd.stop(true);
    }

    @Override
    public Command create() {
        return new NashhornShell();
    }
     
}
