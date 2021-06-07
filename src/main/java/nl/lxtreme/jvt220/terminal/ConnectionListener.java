package nl.lxtreme.jvt220.terminal;

public interface ConnectionListener {

  void onException(Throwable e);
  
  void onConnectionClosed();
  
  void onConnection();
}
