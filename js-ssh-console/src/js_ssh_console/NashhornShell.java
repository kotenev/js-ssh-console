package js_ssh_console;

import java.io.*;
import javax.script.*;
import jline.console.ConsoleReader;
import org.apache.sshd.server.*;

/**
 * Shell for an individual connection. Method starts() creates a thread that handles the shell io.
 */
public class NashhornShell implements Command, Runnable {

    private OutputStream _out;
    private InputStream _in;
    private ExitCallback _callback;
      
    public void destroy() {  _callback.onExit(0); }

    public void setErrorStream(OutputStream err) {}

    public void setExitCallback(ExitCallback callback) { _callback = callback; }

    public void setInputStream(InputStream in) { _in = in; }

    public void setOutputStream(OutputStream out) { _out = out; }

    public void start(Environment env) throws IOException {
        new Thread(this).start(); 
    }

    public void run() {
        try {
            // A tool stream that adds a carriage return for every line feed
            // for console output
            OutputStream crlfOut = new FilterOutputStream(_out) {
                private int _lastChar;
                public void write(int b) throws IOException {
                    if (_lastChar != '\r' && b == '\n') {
                        out.write('\r');
                        out.write('\n');
                    } else {
                        out.write(b);
                    }
                    _lastChar = b;
                }
            };

            // The JLine console reader, handling console input/output
            ConsoleReader reader = new ConsoleReader(_in, crlfOut);
            try {
                reader.setPrompt("MyShell> ");
                
                // Creating the script engine and adding the context object
                ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
                engine.put("Runtime", Runtime.getRuntime());
                
                // Read shell input by line, evaluate on the script engine, print result
                while (true) {
                    String line = reader.readLine();
                    if ("exit".equals(line)) { // Command "exit" terminates the shell.
                        break;
                    }
                    try {
                       Object result = engine.eval(line);
                       if (result != null) {
                           reader.println(String.valueOf(result));
                       }
                    }
                    catch (ScriptException e) {
                       reader.println(e.getMessage());
                    }
                }
                
                // Terminate the shell with exit code 0
                _callback.onExit(0);
            }
            finally {
                reader.shutdown();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            _callback.onExit(1, e.getMessage());
        }
    }
}