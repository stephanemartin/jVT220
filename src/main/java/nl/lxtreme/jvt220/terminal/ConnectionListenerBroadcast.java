package nl.lxtreme.jvt220.terminal;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ConnectionListenerBroadcast implements ConnectionListener {

  private final Set<ConnectionListener> listeners = ConcurrentHashMap.newKeySet();

  @Override
  public void onException(Throwable e) {
    notifyOf(l -> l.onException(e));
  }

  @Override
  public void onConnectionClosed() {
    notifyOf(ConnectionListener::onConnectionClosed);
  }

  @Override
  public void onConnection() {
    notifyOf(ConnectionListener::onConnection);
  }

  private void notifyOf(Consumer<? super ConnectionListener> event) {
    listeners.forEach(event);
  }

  public void add(ConnectionListener listener) {
    listeners.add(listener);
  }

  public void remove(ConnectionListener listener) {
    listeners.remove(listener);
  }
}
