package nl.lxtreme.jvt220.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

public class VTProxyController {

  private final String address;
  private final int port;
  private final long timeout;
  private TelnetClient client;
  private InputStream input;
  private OutputStream output;
  private ConnectionListener connectionListener;
  
  public VTProxyController(String address, int port, long timeout, String terminalType) {
    this.address = address;
    this.port = port;
    this.client = new TelnetClient(terminalType);
    this.timeout = timeout;
    setTelnetConfiguration(terminalType);
  }

  private void setTelnetConfiguration(String terminalType) {
    try {
      client.addOptionHandler(
          new TerminalTypeOptionHandler(terminalType, false, false, true, false));
      client.addOptionHandler(
          new EchoOptionHandler(true, false, true, true));
      client.addOptionHandler(
          new SuppressGAOptionHandler(true, true, true, true));
    } catch (InvalidTelnetOptionException | IOException e) {
      connectionListener.onException(e);
    }
  }

  public void setConnectionListener(ConnectionListener connectionListener) {
    this.connectionListener = connectionListener;
  }

  public void disconnect() throws IOException {
    if (connectionListener!=null) {
        connectionListener.onConnectionClosed();
    }
    client.disconnect();
  }

  public void connect() {
    try {
      if (client.isConnected()) {
        return;
      }
      client.setConnectTimeout((int) timeout);
      client.connect(address, port);
      
      this.input = client.getInputStream();
      this.output = client.getOutputStream();
    } catch (IOException e) {
      connectionListener.onException(e);
    }
  }

  public InputStream getInput() {
    return input;
  }

  public OutputStream getOutput() {
    return output;
  }
  
}
