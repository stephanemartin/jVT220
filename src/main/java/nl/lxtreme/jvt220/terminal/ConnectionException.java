package nl.lxtreme.jvt220.terminal;

import java.io.IOException;

public class ConnectionException extends IOException {

  public ConnectionException(Throwable cause, String server) {
    super("Error connecting to the sever: " + server, cause);
  }

}
