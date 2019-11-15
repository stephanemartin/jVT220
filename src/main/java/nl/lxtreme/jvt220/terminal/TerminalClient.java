package nl.lxtreme.jvt220.terminal;

import java.awt.Dimension;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import nl.lxtreme.jvt220.terminal.vt220.VT220Terminal;

public class TerminalClient {

  private VTProxyController client;
  private VT220Terminal terminal;
  private ConnectionListener connectionListener;

  public TerminalClient(Dimension screenSize, ScreenChangeListener changeListener) {
    //re-think about passing through parameters the changeListener
    this.terminal = new VT220Terminal(screenSize.width, screenSize.height);
    SwingFrontendProxy swingFrontendProxy = new SwingFrontendProxy(changeListener);
    swingFrontendProxy.setTerminal(terminal);
    terminal.setFrontend(swingFrontendProxy);
  }

  public void connect(String address, int port, long timeout, String terminalType) {
    client = new VTProxyController(address, port, timeout, terminalType);
    client.connect();

    if (connectionListener != null) {
      connectionListener.onConnection();
    }
    
    try {
      terminal.getFrontend().connect(client.getInput(), client.getOutput());
    } catch (IOException e) {
      connectionListener.onException(e);
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
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<Integer> future = executor.submit(() -> terminal.write(text));
    try {
      future.get(10000, TimeUnit.MILLISECONDS);
    } catch (InterruptedException | ExecutionException e) {
      connectionListener.onException(e);
    } catch (TimeoutException e) {
      e.initCause(new Throwable("Input took too long reaching the server"));
      connectionListener.onException(e);
    }

  }

  public String getScreen() {
    return terminal.toString();
  }


  public void setConnectionListener(ConnectionListener connectionListener) {
    this.connectionListener = connectionListener;
  }
}
