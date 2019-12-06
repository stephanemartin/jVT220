package nl.lxtreme.jvt220.terminal;

public interface ConnectionStateListener {

  void onException(Throwable e);
  
  void onLogger(String logMessage, Class<?> clazz);
}
