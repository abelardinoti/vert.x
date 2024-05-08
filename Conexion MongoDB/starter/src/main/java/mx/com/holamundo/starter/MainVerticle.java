package mx.com.holamundo.starter;

import java.util.HashSet;
import java.util.Set;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.TimeoutHandler;

public class MainVerticle extends AbstractVerticle {
	
  private static MongoClient mongoClient;
  
  
  public static Set<String> getAllowedHeaders(){
	  Set<String> allowedHeaders = new HashSet<>();
	  
	  allowedHeaders.add("Accept");
	  allowedHeaders.add("Access-Control-Allow-Origin");
	  
	  return allowedHeaders;
  }
  
  public static Set<HttpMethod> getAllowedMethod(){
	  
	  Set<HttpMethod> allowedMethod = new HashSet<>();
	  
	  allowedMethod.add(HttpMethod.GET);
	  allowedMethod.add(HttpMethod.POST);
	  allowedMethod.add(HttpMethod.OPTIONS);
	  allowedMethod.add(HttpMethod.DELETE);
	  allowedMethod.add(HttpMethod.PATCH);
	  allowedMethod.add(HttpMethod.PUT);
	  
	  return allowedMethod;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
	  
	setConnectionMongo(this.getVertx(), config());
	
	Router router = Router.router(vertx);
	router.route().handler(
		CorsHandler.create("*").allowedHeaders(getAllowedHeaders()).allowedMethods(getAllowedMethod()));
	  
	
	router.route("/").handler(routingContext -> {
		HttpServerResponse response = routingContext.response();
		response.putHeader("Content-Type", "text/html")
		.end("<h1>Hola bienvenido al curso de Vert.x por AbelardinoTI youtube</h1>");
	});
	
	router.route().handler(BodyHandler.create());
	router.route().handler(TimeoutHandler.create(6000));
	
	router.post("/create/collection/:name").handler(this::crearColeccion);
	
    vertx.createHttpServer().requestHandler(router).listen(5000, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 5000");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
  
	public static void setConnectionMongo(Vertx vertx, JsonObject config) {

		try {
			if (mongoClient == null) {
				config.put("db_name", "local").put("connection_string", "mongodb://localhost:27017");

				if (System.getenv("USER_MOGO") != null)
					config.put("username", System.getenv("USER_MOGO"));

				if (System.getenv("PASSWORD_MOGO") != null)
					config.put("password", System.getenv("PASSWORD_MOGO"));

				mongoClient = MongoClient.createShared(vertx, config);
			}

		} catch (Exception e) {
			System.out.println(e);
		}

	}
	
	private void crearColeccion(RoutingContext  routingContext) {
		String nombreColeccion = routingContext.pathParam("name");
		
		mongoClient.createCollection(nombreColeccion, res -> {
			if(res.succeeded()) {
				routingContext.response().setStatusCode(201).end("Colección creada correctamente");
			} else {
				routingContext.response().setStatusCode(500).end("Fallo al crear la colección: " + res.cause().getMessage());
			}
		});
		
	}
	
	
}
