package nl.lxtreme.jvt220.terminal;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.BitSet;
import nl.lxtreme.jvt220.terminal.ITerminal.ITextCell;
import nl.lxtreme.jvt220.terminal.swing.SwingFrontend;

public class SwingFrontendProxy implements ITerminalFrontend {

  private SwingFrontend swingFrontend;
  private ScreenChangeListener changeListener;

  public SwingFrontendProxy(ScreenChangeListener changeListener) {
    this.swingFrontend = new SwingFrontend();
    this.changeListener = changeListener;
  }

  @Override
  public void connect(InputStream inputStream, OutputStream outputStream) throws IOException {
    swingFrontend.connect(inputStream, outputStream);
  }

  @Override
  public void connect(OutputStream outputStream) throws IOException {
    swingFrontend.connect(outputStream);
  }

  @Override
  public void disconnect() throws IOException {
    swingFrontend.disconnect();
  }

  @Override
  public Dimension getMaximumTerminalSize() {
    return swingFrontend.getMaximumTerminalSize();
  }

  @Override
  public Dimension getSize() {
    return swingFrontend.getSize();
  }

  @Override
  public Writer getWriter() {
    return swingFrontend.getWriter();
  }

  @Override
  public boolean isListening() {
    return true;
  }

  @Override
  public void setReverse(boolean reverse) {
    swingFrontend.setReverse(reverse);
  }

  @Override
  public void setSize(int width, int height) {
    swingFrontend.setSize(width, height);
  }

  @Override
  public void setTerminal(ITerminal terminal) {
    swingFrontend.setTerminal(terminal);
  }

  @Override
  public void terminalChanged(ITextCell[] cells, BitSet heatMap) {
    changeListener.screenChanged(swingFrontend.getTerminal().toString());
  }

  @Override
  public void terminalSizeChanged(int columns, int alines) {
  }

  @Override
  public void writeCharacters(Integer... chars) throws IOException {
    swingFrontend.writeCharacters(chars);
  }

  @Override
  public void writeCharacters(CharSequence chars) throws IOException {
    swingFrontend.writeCharacters(chars);
  }
}
