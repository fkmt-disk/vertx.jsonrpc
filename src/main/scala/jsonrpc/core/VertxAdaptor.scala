package jsonrpc.core

import scala.language.implicitConversions

import org.vertx.java.core.AsyncResult
import org.vertx.java.core.AsyncResultHandler
import org.vertx.java.core.Handler
import org.vertx.java.core.SimpleHandler
import org.vertx.java.core.eventbus.EventBus
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonArray
import org.vertx.java.core.json.JsonObject
import org.vertx.java.deploy.Container

/**
 * VertxAdaptor.
 * 
 * @author fkmt.disk@gmail.com
 */
object VertxAdaptor {
  
  implicit def done(fn: (String) => Unit): Handler[String] =
    new Handler[String] {
      override def handle(id: String) = fn(id)
    }
  
  implicit def async(fn: (AsyncResult[Void]) => _): AsyncResultHandler[Void] =
    new AsyncResultHandler[Void] {
      override def handle(result: AsyncResult[Void]) = fn(result)
    }
  
  implicit def handler[T](fn: (T) => _): Handler[T] =
    new Handler[T] {
      override def handle(targetOfHandling: T) = fn(targetOfHandling)
    }
  
  implicit class ContaninerWrapper(container: Container) {
    
    def deployWorker(
        fqcn: String
      , config: JsonObject = null
      , instances: Int = 1
    )(done: Handler[String]) =
      container.deployWorkerVerticle(fqcn, config, instances, done)
    
  }
  
  implicit class EventBusWrapper(ebus: EventBus) {
    
    private type AnyHandler = Handler[_]
    
    private type MessageHandler = Handler[Message[_]]
    
    private type ObjectHandler = Handler[Message[JsonObject]]
    
    private type ArrayHandler = Handler[Message[JsonArray]]
    
    private type AsyncHandler = AsyncResultHandler[Void]
    
    
    def post(address: String, message: JsonObject)(reply: ObjectHandler) =
      ebus.send(address, message, reply)
    
    def post(address: String, message: JsonArray)(reply: ArrayHandler) =
      ebus.send(address, message, reply)
    
    def post(address: String)(reply: ObjectHandler) =
      ebus.send(address, null.asInstanceOf[JsonObject], reply)
    
    def regist(address: String, handler: AnyHandler)(async: AsyncHandler) =
      ebus.registerHandler(address, handler.asInstanceOf[MessageHandler], async)
    
    def unregist(address: String, handler: AnyHandler)(async: AsyncHandler) =
      ebus.unregisterHandler(address, handler.asInstanceOf[MessageHandler], async)
    
  }
  
}