package ru.mail.polis.neron;

import one.nio.http.HttpServer;
import one.nio.server.ServerConfig;
import org.jetbrains.annotations.NotNull;
import ru.mail.polis.KVService;

import java.io.IOException;

/**
 * @author neron
 */
public class OneNioKVService implements KVService {

  @NotNull
  private final HttpServer httpServer;

  public OneNioKVService(int port, @NotNull final MyDAO dao) throws IOException {
    this.httpServer = new OneNioHttpServer(ServerConfig.from("http://localhost:" + port), dao);
  }


  @Override
  public void start() {
    this.httpServer.start();
  }

  @Override
  public void stop() {
    this.httpServer.stop();
  }
}
