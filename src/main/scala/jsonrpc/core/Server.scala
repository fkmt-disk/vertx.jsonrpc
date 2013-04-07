package jsonrpc.core

import scala.collection.JavaConversions._
import org.vertx.java.core.Handler
import org.vertx.java.core.http.HttpServerRequest
import org.vertx.java.deploy.Verticle
import org.vertx.java.core.json.JsonObject
import org.vertx.java.core.buffer.Buffer

class Server extends Verticle {
  
  override def start(): Unit = {
    val config = new Config(container.getConfig)
    
    config.verticles.entrySet.foreach { v =>
      val name = v.getKey
      val conf = v.getValue.asInstanceOf[Map[String, Object]]
      //println(s"${setting.getClass}  ${setting}")
      
      //container.deployWorkerVerticle(conf.get("class_name"), config, instances)
      
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
  
  
  class Config(json: JsonObject) {
    
    val port = json.getInteger("port")
    
    val charset = json.getString("charset")
    
    val package_name = json.getString("package_name")
    
    val verticles = json.getObject("verticles").toMap
    
  }
  
  import scala.language.implicitConversions
  
  implicit def handler[T](fn: (T) => _): Handler[T] = {
    new Handler[T] {
      override def handle(targetOfHandling: T): Unit = fn(targetOfHandling)
    }
  }
  
  implicit def toJson(text: String): JsonObject = new JsonObject(text)
  
}
