package nl.lxtreme.jvt220.terminal;

import java.awt.Dimension;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;
import nl.lxtreme.jvt220.terminal.swing.SwingFrontend;
import nl.lxtreme.jvt220.terminal.vt220.VT220Terminal;

public class TerminalClient {

  private VTProxyController client;
  private VT220Terminal terminal;
  private Thread clientThread;
  private ConnectionListener connectionListener;

  public TerminalClient(Dimension screenSize) {
    this.terminal = new VT220Terminal(screenSize.width, screenSize.height);
    SwingFrontend swingFrontend = new SwingFrontend(StandardCharsets.UTF_8.toString());
    swingFrontend.setTerminal(terminal);
    terminal.setFrontend(swingFrontend);
  }

  public void connect(String address, int port, int timeout, String terminalType) {
    this.client = new VTProxyController(address, port, timeout, terminalType);
    clientThread = new Thread(client);
    clientThread.start();
    BooleanSupplier condition = () -> client.isRunning();
    try {
      waitUntil(condition, timeout);
    } catch (TimeoutException e) {
      if (connectionListener != null) {
        connectionListener.onException(e);
      }
    }

    if (connectionListener != null) {
      connectionListener.onConnection();
    }
  }

  private void waitUntil(BooleanSupplier running, int timeout) throws TimeoutException {
    long startTime = System.currentTimeMillis();
    while (!running.getAsBoolean()) {
      System.out.println(running.getAsBoolean());
      if (System.currentTimeMillis() - startTime > timeout) {
        throw new TimeoutException("Timeout while waiting for connect");
      }
    }
  }

  public void disconnect() throws IOException, InterruptedException {
    if (client != null) {
      client.disconnect();
      clientThread.interrupt();
      clientThread.join();
      if (connectionListener != null) {
        connectionListener.onConnectionClosed();
      }
    }

  }


  public void sendTextByCurrentCursorPosition(String text) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<Integer> future = executor.submit(() -> terminal.write(text));
    try {
      future.get(2000, TimeUnit.MILLISECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      e.printStackTrace();
    }

  }

  public String getScreen() throws InterruptedException, ExecutionException {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    Callable<String> task = () -> terminal.toString();
    // the delay should be dynamic obtained
    return executorService.schedule(task, 5000, TimeUnit.MILLISECONDS).get();
  }

  public void setConnectionListener(ConnectionListener connectionListener) {
    this.connectionListener = connectionListener;
  }
}
