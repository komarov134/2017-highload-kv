package ru.mail.polis.neron;

import one.nio.http.HttpServer;
import one.nio.http.HttpSession;
import one.nio.http.Param;
import one.nio.http.Path;
import one.nio.http.Request;
import one.nio.http.Response;
import one.nio.server.ServerConfig;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * @author neron
 */
public class OneNioHttpServer extends HttpServer {

  @NotNull
  private static final String BASE_PATH = "/v0";

  private static final Response ACCEPTED =
      new Response(Response.ACCEPTED, Response.EMPTY);
  private static final Response CREATED =
      new Response(Response.CREATED, Response.EMPTY);
  private static final Response METHOD_NOT_ALLOWED =
      new Response(Response.METHOD_NOT_ALLOWED, Response.EMPTY);

  @NotNull
  private final MyDAO dao;

  public OneNioHttpServer(
      @NotNull final ServerConfig config,
      @NotNull final MyDAO dao) throws IOException {
    super(config);
    this.dao = dao;
  }

  @Path(BASE_PATH + "/status")
  public Response handleStatus() {
    return Response.ok("ONLINE");
  }

  @Path(BASE_PATH + "/entity")
  public Response handleEntity(
      @Param(value = "id", required = true) String id,
      @Param(value = "replicas") String replicas,
      Request request) throws IOException {
    if (id.isEmpty()) {
      throw new IllegalArgumentException("Empty 'id' parameter");
    }
    final Response response;
    switch (request.getMethod()) {
      case Request.METHOD_GET:
        response = Response.ok(dao.get(id));
        break;

      case Request.METHOD_DELETE:
        dao.delete(id);
        response = ACCEPTED;
        break;

      case Request.METHOD_PUT:
        dao.upsert(id, request.getBody());
        response = CREATED;
        break;

      default:
        response = METHOD_NOT_ALLOWED;
        break;
    }
    return response;
  }

  @Override
  public void handleRequest(Request request, HttpSession session) throws IOException {
    try {
      super.handleRequest(request, session);
    } catch (NoSuchElementException e) {
      session.sendError(Response.NOT_FOUND, null);
    } catch (IllegalArgumentException e) {
      session.sendError(Response.BAD_REQUEST, null);
    } catch (RuntimeException e) {
      session.sendError(Response.INTERNAL_ERROR, null);
    }
  }

}
