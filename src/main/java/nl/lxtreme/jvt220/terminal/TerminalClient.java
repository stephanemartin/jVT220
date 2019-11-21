package nl.lxtreme.jvt220.terminal;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.Optional;
import nl.lxtreme.jvt220.terminal.vt220.VT220Terminal;
import org.apache.commons.net.telnet.VT420Client;

public class TerminalClient {

  private VT420Client client;
  private VT220Terminal terminal;
  private SwingFrontendProxy swingFrontendProxy;
  private ConnectionListener connectionListener;

  public TerminalClient(Dimension screenSize) {
    this.terminal = new VT220Terminal(screenSize.width, screenSize.height);
    swingFrontendProxy = new SwingFrontendProxy();
    swingFrontendProxy.setTerminal(terminal);
    terminal.setFrontend(swingFrontendProxy);
  }

  public void connect(String address, int port, int timeout, String terminalType)
      throws IOException {
    client = new VT420Client(terminalType);
    client.setConnectTimeout(timeout);
    client.connect(address, port);
    terminal.getFrontend().connect(client.getInputStream(), client.getOutputStream());
    if (connectionListener != null) {
      connectionListener.onConnection();
    }
  }

  public void disconnect() throws IOException {
    client.disconnect();
    if (connectionListener != null) {
      connectionListener.onConnectionClosed();
    }
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

  public void setConnectionListener(ConnectionListener connectionListener) {
    this.connectionListener = connectionListener;
  }
}
