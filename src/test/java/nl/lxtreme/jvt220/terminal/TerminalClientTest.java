package nl.lxtreme.jvt220.terminal;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import us.abstracta.wiresham.Flow;
import us.abstracta.wiresham.VirtualTcpService;

@RunWith(MockitoJUnitRunner.class)
public class TerminalClientTest {

  private static final String TERMINAL_MODEL_TYPE_TWO = "VT420-7";
  private static final Dimension SCREEN_DIMENSIONS = new Dimension(80, 24);
  private static final String SSL_TYPE = "TLS";
  private static final String LOGIN_FLOW = "/loginVT420.yml";
  private static final long TIMEOUT_MILLIS = 100000;
  private static final String SERVER_ADDRESS = "localhost";
  private static final String WELCOME_SCREEN_FILE_PATH = "user-welcome-screen.txt";
  private VirtualTcpService service = new VirtualTcpService();
  private TerminalClient client;
  private ScreenChangeListener listener;
  private CountDownLatch latch;

  @Before
  public void setUp() {
    client = new TerminalClient(SCREEN_DIMENSIONS, TERMINAL_MODEL_TYPE_TWO);
  }

  @Test
  public void shouldGetWelcomeScreenWhenConnectionViaSSL() throws Exception {
    setupSslServerConnection();
    client.setSocketFactory(buildSslContext().getSocketFactory());
    connectClient();
    awaitForScreen();
    assertThat(client.getScreen())
        .isEqualTo(getFileContent(WELCOME_SCREEN_FILE_PATH));
  }

  private void setupSslServerConnection() throws IOException {
    //properties needs to be set before lunching VirtualService
    System.setProperty("javax.net.ssl.keyStore", getResourceFilePath("/keystore.jks"));
    System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
    service.setSslEnabled(true);
    startServiceWithFlow(LOGIN_FLOW);
  }

  private void connectClient() throws IOException, InvalidTelnetOptionException {
    setupListener();
    client.addScreenChangeListener(listener);
    client.connect(SERVER_ADDRESS, service.getPort(), (int) TIMEOUT_MILLIS);
  }

  private void startServiceWithFlow(String flowPath) throws IOException {
    service.setFlow(Flow.fromYml(new File(getResourceFilePath(flowPath))));
    service.start();
  }

  private String getResourceFilePath(String resourcePath) {
    return getClass().getResource(resourcePath).getFile();
  }

  private SSLContext buildSslContext() throws GeneralSecurityException {
    SSLContext sslContext = SSLContext.getInstance(SSL_TYPE);
    TrustManager trustManager = new X509TrustManager() {

      public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
      }

      public void checkClientTrusted(
          X509Certificate[] certs, String authType) {
      }

      public void checkServerTrusted(
          X509Certificate[] certs, String authType) {
      }
    };
    sslContext.init(null, new TrustManager[]{trustManager},
        new SecureRandom());
    return sslContext;
  }

  public void setupListener() {
    latch = new CountDownLatch(1);
    listener = screen -> latch.countDown();
  }

  public void awaitForScreen() throws InterruptedException {
    latch.await(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
  }

  private String getFileContent(String resourceFile) throws IOException {
    return Resources.toString(Resources.getResource(resourceFile),
        Charsets.UTF_8);
  }
}
