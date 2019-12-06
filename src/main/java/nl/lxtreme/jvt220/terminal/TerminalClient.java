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

  public TerminalClient(Dimension screenSize, String terminalType) {
    this.terminal = new VT220Terminal(screenSize.width, screenSize.height);
    client = new VT420Client(terminalType);
    swingFrontendProxy = new SwingFrontendProxy();
    swingFrontendProxy.setTerminal(terminal);
    terminal.setFrontend(swingFrontendProxy);
  }

  public void connect(String address, int port, int timeout)
      throws IOException {
    client.setConnectTimeout(timeout);
    client.connect(address, port);
    terminal.getFrontend().connect(client.getInputStream(), client.getOutputStream());
  }

  public void disconnect() throws IOException {
    client.disconnect();
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

  public void setConnectionStateListener(ConnectionStateListener listener) {
    client.setConnectionStateListener(listener);
    swingFrontendProxy.setConnectionStateListener(listener);
  }

}
