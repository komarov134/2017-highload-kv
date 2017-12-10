package ru.mail.polis.neron;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import ru.mail.polis.KVService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.NoSuchElementException;

/**
 * @author neron
 */
public class MyService implements KVService {

  private final HttpServer httpServer;

  private static final String QUERY_ID = "id=";

  private static String extractId(@NotNull final String query) {
    if (!query.startsWith(QUERY_ID)) {
      throw new IllegalArgumentException("Query should starts with 'id='");
    }
    final String id = query.substring(QUERY_ID.length());
    if (id.isEmpty()) {
      throw new IllegalArgumentException("id is empty");
    }
    return id;
  }

  public MyService(int port, @NotNull final MyDAO dao) throws IOException {
    this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);

    this.httpServer.createContext(
        "/v0/status",
        http -> {
          final String response = "ONLINE";
          http.sendResponseHeaders(200, response.length());
          http.getResponseBody().write(response.getBytes());
          http.close();
        }
    );

    this.httpServer.createContext(
        "/v0/entity",
        new ErrorHandler(
          http -> {
            final String id = extractId(http.getRequestURI().getQuery());
            switch (http.getRequestMethod()) {
              case "GET":
                final byte[] getValue = dao.get(id);
                http.sendResponseHeaders(200, getValue.length);
                http.getResponseBody().write(getValue);
                break;

              case "PUT":
                final int contentLength =
                    Integer.valueOf(http.getRequestHeaders().getFirst("Content-Length"));
                final byte[] putValue = new byte[contentLength];
                if (contentLength != 0 && http.getRequestBody().read(putValue) != putValue.length) {
                  throw new IOException("Can't read body from request");
                }
                dao.upsert(id, putValue);
                http.sendResponseHeaders(201, 0);
                break;

              case "DELETE":
                dao.delete(id);
                http.sendResponseHeaders(202, 0);
                break;

              default:
                http.sendResponseHeaders(405, 0);
                break;
            }
            http.close();
          }
        )
    );
  }

  private static class ErrorHandler implements HttpHandler {

    private final HttpHandler delegate;

    private ErrorHandler(final HttpHandler delegate) {
      this.delegate = delegate;
    }

    @Override
    public void handle(final HttpExchange httpExchange) throws IOException {
      try {
        delegate.handle(httpExchange);
      } catch (NoSuchElementException e) {
        httpExchange.sendResponseHeaders(404, 0);
        httpExchange.close();
      } catch (IllegalArgumentException e) {
        httpExchange.sendResponseHeaders(400, 0);
        httpExchange.close();
      }
    }
  }

  @Override
  public void start() {
    this.httpServer.start();
  }

  @Override
  public void stop() {
    this.httpServer.stop(0);
  }
}
