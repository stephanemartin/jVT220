package nl.lxtreme.jvt220.terminal;

public interface ConnectionListener {
  
  void onConnection();
  
  void onException(Exception e);
  
  void onConnectionClosed();

}
