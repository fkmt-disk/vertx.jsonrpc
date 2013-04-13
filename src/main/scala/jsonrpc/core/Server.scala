package jsonrpc.core

import org.vertx.java.core.buffer.Buffer
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.http.HttpServerRequest
import org.vertx.java.core.json.JsonArray
import org.vertx.java.core.json.JsonObject
import org.vertx.java.deploy.Verticle

import jsonrpc.core.JsonRpcError.InternalError
import jsonrpc.core.JsonRpcRequest.findId
import jsonrpc.core.JsonRpcResponse.contentType
import jsonrpc.core.JsonRpcResponse.makeError
import jsonrpc.core.JsonRpcResponse.makeResult
import jsonrpc.core.VertxAdaptor.EventBusWrapper
import jsonrpc.core.VertxAdaptor.done
import jsonrpc.core.VertxAdaptor.handler

/**
 * Server.
 * 
 * @author fkmt.disk@gmail.com
 */
class Server extends Verticle {
  
  override def start(): Unit = {
    val log = container.getLogger
    
    val ebus = vertx.eventBus
    
    val config = new Config(container.getConfig)
    
    config.workers.foreach { worker =>
      container.deployWorkerVerticle(
          worker.className
        , worker.config
        , worker.instances
        , done(() => log.debug(s"${worker.name} has been deployed"))
      )
    }
    
    vertx.createHttpServer.requestHandler { req: HttpServerRequest =>
      val res = req.response
      
      req.bodyHandler { body: Buffer =>
        val text = body.toString
        
        val requests = try {
          Option(JsonRpcRequest.parse(text))
        }
        catch {
          case e: JsonRpcException =>
            res.headers.put("Content-Type", contentType(config.charset))
            res.end(makeError(findId(text), e.toJson).toString)
            None
          case t: Throwable =>
            res.headers.put("Content-Type", contentType(config.charset))
            res.end(makeError(findId(text), InternalError, t).toString)
            None
        }
        
        requests.foreach { jsonReq =>
          jsonReq.params match {
            case param: JsonArray =>
              ebus.post(jsonReq.method, param) { reply: Message[JsonArray] =>
                res.headers.put("Content-Type", contentType(config.charset))
                res.end(makeResult(jsonReq.id, reply.body).toString)
              }
            case param: JsonObject =>
              ebus.post(jsonReq.method, param) { reply: Message[JsonObject] =>
                res.headers.put("Content-Type", contentType(config.charset))
                val body = reply.body
                log.info(s"server reply: " + body)
                res.end(makeResult(jsonReq.id, body).toString)
              }
            case null =>
              ebus.post(jsonReq.method) { reply: Message[JsonObject] =>
                res.headers.put("Content-Type", contentType(config.charset))
                res.end(makeResult(jsonReq.id, reply.body).toString)
              }
          }
        }
      }
      
    } .listen(config.port)
    
  }
  
  override def stop(): Unit = {
    container.getLogger.info(s"${getClass.getName} stop")
  }
  
}
