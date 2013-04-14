package jsonrpc.sample

import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonObject
import org.vertx.java.deploy.Verticle
import jsonrpc.core.VertxAdaptor._
import org.vertx.java.core.Handler
import org.vertx.java.core.AsyncResult
import org.vertx.java.core.AsyncResultHandler

class AddService extends Verticle {
  
  override def start() {
    val log = container.getLogger
    
    val ebus = vertx.eventBus
    
    val config = container.getConfig
    
    val name = config.getString("name")
    
    ebus.regist(name, service) { result: AsyncResult[Void] =>
      if (result.failed)
        log.error(s"[AddService] registerHandler faild", result.exception)
      else
        log.info(s"[AddService] registered")
    }
  }
  
  override def stop() {
    vertx.eventBus.unregisterHandler(container.getConfig.getString("name"), service)
  }
  
  private val service = new Handler[Message[JsonObject]] {
    override def handle(message: Message[JsonObject]) {
      val body = message.body
      
      val num1 = body.getInteger("num1")
      val num2 = body.getInteger("num2")
      
      val reply = new JsonObject
      reply.putNumber("answer", num1 + num2)
      
      message.reply(reply)
    }
  }
  
}
