package jsonrpc.core

import scala.collection.JavaConversions._
import org.vertx.java.core.Handler
import org.vertx.java.core.http.HttpServerRequest
import org.vertx.java.deploy.Verticle
import org.vertx.java.core.json.JsonObject
import org.vertx.java.core.buffer.Buffer
import org.vertx.java.core.SimpleHandler

class Server extends Verticle {
  
  override def start(): Unit = {
    val log = container.getLogger
    
    val config = new Config(container.getConfig)
    
    config.workers.foreach { worker =>
      container.deployWorkerVerticle(
          worker.className,
          worker.config,
          worker.instances,
          done(() => log.debug(s"${worker.name} has been deployed"))
      )
    }
    
    //val ebus = vertx.eventBus
    
    vertx.createHttpServer.requestHandler { req: HttpServerRequest =>
      /*
      req.bodyHandler { body: Buffer =>
        //println(body.toString)
        
        val json = new JsonObject(body.toString)
        
        println(json.toString)
        
        var qux = json.getArray("qux")
        
        var l = List("a","b","c")
        val v = l(0)
      }
      */
      req.response.headers.put("Content-Type", s"text/html; charset=${config.charset}")
      req.response.end(<html><body><h3>test!</h3></body></html>.toString)
    } .listen(config.port)
  }
  
  override def stop(): Unit = {
    container.getLogger.info(s"${getClass.getName} stop")
  }
  
  private def done(fn: () => Unit): Handler[String] = {
    new SimpleHandler {
      override def handle = fn()
    }.asInstanceOf[Handler[String]]
  }
  
  import scala.language.implicitConversions
  
  implicit def handler[T](fn: (T) => _): Handler[T] = {
    new Handler[T] {
      override def handle(targetOfHandling: T): Unit = fn(targetOfHandling)
    }
  }
  
  implicit def toJson(text: String): JsonObject = new JsonObject(text)
  
}
