package nl.lxtreme.jvt220.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

public class VTProxyController implements Runnable {

  private final String address;
  private final int port;
  private final int timeout;
  private TelnetClient client;
  private InputStream input;
  private OutputStream output;
  private ConnectionListener connectionListener;

  public VTProxyController(String address, int port, int timeout, String terminalType) {
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
      e.printStackTrace();
    }
  }

  public void setConnectionListener(ConnectionListener connectionListener) {
    this.connectionListener = connectionListener;
  }

  public void disconnect() throws IOException {
    if (!client.isConnected()) {
      return;
    }
    client.disconnect();
  }

  @Override
  public void run() {
    try {
      if (client.isConnected()) {
        return;
      }
      client.setConnectTimeout(timeout);
      client.connect(address, port);

      this.input = client.getInputStream();
      this.output = client.getOutputStream();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public InputStream getInput() {
    return input;
  }

  public OutputStream getOutput() {
    return output;
  }

  public boolean isRunning() {
    return client.isConnected();
  }
}
