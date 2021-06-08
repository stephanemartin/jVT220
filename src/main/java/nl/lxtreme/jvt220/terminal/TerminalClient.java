package nl.lxtreme.jvt220.terminal;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.Optional;
import javax.net.SocketFactory;
import nl.lxtreme.jvt220.terminal.vt220.VT220Terminal;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.VT420Client;

public class TerminalClient {

  private final VT420Client client;
  private final VT220Terminal terminal;
  private final SwingFrontendProxy swingFrontendProxy;
  private final ConnectionListenerBroadcast connectionListenerBroadcast;

  public TerminalClient(Dimension screenSize, String terminalType) {
    this.terminal = new VT220Terminal(screenSize.width, screenSize.height);
    client = new VT420Client(terminalType);
    connectionListenerBroadcast = new ConnectionListenerBroadcast();
    swingFrontendProxy = new SwingFrontendProxy();
    swingFrontendProxy.setTerminal(terminal);
    swingFrontendProxy.setConnectionListener(connectionListenerBroadcast);
    terminal.setFrontend(swingFrontendProxy);
  }

  public void connect(String address, int port, int timeout)
      throws ConnectionException {
    try {
      client.setupOptionHandlers();
      client.setConnectTimeout(timeout);
      client.connect(address, port);
      connectionListenerBroadcast.onConnection();
      terminal.getFrontend().connect(client.getInputStream(), client.getOutputStream());
    } catch (InvalidTelnetOptionException | IOException e) {
      throw new ConnectionException(e, String.format("%s:%s", address, port));
    }
  }

  public void disconnect() throws IOException {
    client.disconnect();
    swingFrontendProxy.disconnect();
  }

  public void sendTextByCurrentCursorPosition(String text) throws IOException {
    terminal.write(text);
  }

  public String getScreen() {
    return terminal.toString();
  }

  public void addScreenChangeListener(ScreenChangeListener listener) {
    swingFrontendProxy.addScreenChangeListener(listener);
  }

  public void removeScreenChangeListener(ScreenChangeListener listener) {
    swingFrontendProxy.removeScreenChangeListener(listener);
  }

  public Optional<Point> getCursorPosition() {
    ICursor cursor = terminal.getCursor();
    return cursor.isVisible() ? Optional.of(new Point(cursor.getX(), cursor.getY()))
        : Optional.empty();
  }

  public Dimension getScreenSize() {
    return new Dimension(terminal.getWidth(), terminal.getHeight());
  }

  public void addConnectionListener(ConnectionListener listener) {
    connectionListenerBroadcast.add(listener);
  }
 
  public void removeConnectionListener(ConnectionListener listener) {
    connectionListenerBroadcast.remove(listener);
  }

  public void setSocketFactory(SocketFactory socketFactory) {
    client.setSocketFactory(socketFactory);
  }

}
