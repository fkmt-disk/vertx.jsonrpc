package jsonrpc.core

import org.vertx.java.core.Handler
import org.vertx.java.core.SimpleHandler
import org.vertx.java.core.eventbus.EventBus
import org.vertx.java.core.json.JsonObject
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonArray

/**
 * VertxAdaptor.
 * 
 * @author fkmt.disk@gmail.com
 */
object VertxAdaptor {
  
  def done(fn: () => Unit): Handler[String] = {
    new SimpleHandler {
      override def handle = fn()
    }.asInstanceOf[Handler[String]]
  }
  
  implicit def handler[T](fn: (T) => _): Handler[T] = {
    new Handler[T] {
      override def handle(targetOfHandling: T): Unit = fn(targetOfHandling)
    }
  }
  
  implicit class EventBusWrapper(ebus: EventBus) {
    def post(address: String, message: JsonObject)(reply: Handler[Message[JsonObject]]) = ebus.send(address, message, reply)
    def post(address: String, message: JsonArray)(reply: Handler[Message[JsonArray]]) = ebus.send(address, message, reply)
    def post(address: String)(reply: Handler[Message[JsonObject]]) = ebus.send(address, null.asInstanceOf[JsonObject], reply)
  }
  
}