package nl.lxtreme.jvt220.terminal;

import java.awt.Dimension;
import java.io.IOException;
import nl.lxtreme.jvt220.terminal.vt220.VT220Terminal;
import org.apache.commons.net.telnet.VT420Client;

public class TerminalClient {

  private VT420Client client;
  private VT220Terminal terminal;
  private SwingFrontendProxy swingFrontendProxy;

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

}
