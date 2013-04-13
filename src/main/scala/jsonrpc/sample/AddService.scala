package jsonrpc.sample

import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonObject
import org.vertx.java.deploy.Verticle

import jsonrpc.core.VertxAdaptor.handler

class AddService extends Verticle {
  
  override def start() {
    val log = container.getLogger
    
    val ebus = vertx.eventBus
    
    val config = container.getConfig
    
    val name = config.getString("name")
    
    ebus.registerHandler(name, service)
  }
  
  override def stop() {
    vertx.eventBus.unregisterHandler(container.getConfig.getString("name"), service)
  }
  
  private val service = { message: Message[JsonObject] =>
    val log = container.getLogger
    
    val body = message.body
    
    val num1 = body.getInteger("num1")
    val num2 = body.getInteger("num2")
    
    val reply = new JsonObject
    reply.putNumber("answer", num1 + num2)
    
    log.info(s"add relpy : ${reply}")
    
    message.reply(reply)
  }
  
}
