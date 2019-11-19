package nl.lxtreme.jvt220.terminal;

import java.awt.Dimension;
import java.io.IOException;
import nl.lxtreme.jvt220.terminal.vt220.VT220Terminal;

public class TerminalClient {

  private VTProxyController client;
  private VT220Terminal terminal;
  private ConnectionListener connectionListener;
  private SwingFrontendProxy swingFrontendProxy;

  public TerminalClient(Dimension screenSize) {
    this.terminal = new VT220Terminal(screenSize.width, screenSize.height);
    swingFrontendProxy = new SwingFrontendProxy();
    swingFrontendProxy.setTerminal(terminal);
    terminal.setFrontend(swingFrontendProxy);
  }

  public void connect(String address, int port, long timeout, String terminalType) {
    client = new VTProxyController(address, port, timeout, terminalType);
    client.connect();
    try {
      terminal.getFrontend().connect(client.getInput(), client.getOutput());
    } catch (IOException e) {
      connectionListener.onException(e);
    }

    if (connectionListener != null) {
      connectionListener.onConnection();
    }
  }

  public void disconnect() {
    if (client != null) {
      try {
        client.disconnect();
      } catch (IOException e) {
        connectionListener.onException(e);
      }
    }

  }


  public void sendTextByCurrentCursorPosition(String text) {
    try {
      terminal.write(text);
    } catch (IOException e) {
      if (connectionListener != null) {
        connectionListener.onException(e);
      }
    }
  }

  }

  public String getScreen() {
    return terminal.toString();
  }


  public void setConnectionListener(ConnectionListener connectionListener) {
    this.connectionListener = connectionListener;
  }

  public void addScreenChangeListener(ScreenChangeListener listener) {
    swingFrontendProxy.addScreenChangeListener(listener);
  }

  public void removeScreenChangeListener(ScreenChangeListener listener) {
    swingFrontendProxy.removeScreenChangeListener(listener);
  }
}
