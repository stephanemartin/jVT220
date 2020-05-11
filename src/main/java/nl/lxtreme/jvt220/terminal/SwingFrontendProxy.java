package nl.lxtreme.jvt220.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import nl.lxtreme.jvt220.terminal.ITerminal.ITextCell;
import nl.lxtreme.jvt220.terminal.swing.SwingFrontend;

public class SwingFrontendProxy extends SwingFrontend {

  private static final int MAX_THREAD_POOL = 150;
  private List<ScreenChangeListener> screenChangeListeners = new CopyOnWriteArrayList<>();
  private ExecutorService swingThreadPool;

  public SwingFrontendProxy() {
    super();
  }

  @Override
  public void connect(InputStream inputStream, OutputStream outputStream) throws IOException {
    if (inputStream == null) {
      throw new IllegalArgumentException("Input stream cannot be null!");
    }
    if (outputStream == null) {
      throw new IllegalArgumentException("Output stream cannot be null!");
    }

    disconnect();

    m_writer = new OutputStreamWriter(outputStream, m_encoding);
    ThreadFactory threadFactory = r -> new Thread(r, "jVT-Frontend-Workers");
    m_inputStreamWorker = new InputStreamWorker(inputStream, m_encoding);
    swingThreadPool = Executors.newFixedThreadPool(MAX_THREAD_POOL, threadFactory);
    swingThreadPool.submit(m_inputStreamWorker);
    setEnabled(true);
  }

  @Override
  public void disconnect() {
    if (super.m_inputStreamWorker != null) {
      if (swingThreadPool != null) {
        swingThreadPool.shutdownNow();
      }
      m_inputStreamWorker.cancel(true /* mayInterruptIfRunning */);
      m_inputStreamWorker = null;
    }
  }

  @Override
  public boolean isListening() {
    return true;
  }

  @Override
  public void terminalChanged(ITextCell[] cells, BitSet heatMap) {
    screenChangeListeners.forEach(l -> l.screenChanged(super.getTerminal().toString()));
  }

  @Override
  public void terminalSizeChanged(int columns, int alines) {

  }

  public void addScreenChangeListener(ScreenChangeListener listener) {
    screenChangeListeners.add(listener);
  }

  public void removeScreenChangeListener(ScreenChangeListener listener) {
    screenChangeListeners.remove(listener);
  }
}
