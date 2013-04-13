package jsonrpc.core

import org.vertx.java.core.buffer.Buffer
import org.vertx.java.core.http.HttpServerRequest
import org.vertx.java.core.json.JsonObject
import org.vertx.java.deploy.Verticle
import jsonrpc.core.JsonRpcError.InternalError
import jsonrpc.core.JsonRpcRequest.findId
import jsonrpc.core.JsonRpcResponse.contentType
import jsonrpc.core.JsonRpcResponse._
import jsonrpc.core.VertxAdaptor._
import org.vertx.java.core.json.JsonArray
import org.vertx.java.core.eventbus.Message

class Server extends Verticle {
  
  override def start(): Unit = {
    val log = container.getLogger
    
    val config = new Config(container.getConfig)
    
    config.workers.foreach { worker =>
      container.deployWorkerVerticle(
          worker.className
        , worker.config
        , worker.instances
        , done(() => log.debug(s"${worker.name} has been deployed"))
      )
    }
    
    val ebus = vertx.eventBus
    
    vertx.createHttpServer.requestHandler { req: HttpServerRequest =>
      val res = req.response
      
      req.bodyHandler { body: Buffer =>
        val text = body.toString
        
        val requests = try {
          JsonRpcRequest.parse(text)
        }
        catch {
          case e: JsonRpcException =>
            res.headers.put("Content-Type", contentType(config.charset))
            res.end(makeError(findId(text), e.toJson).toString)
            Nil
          case t: Throwable =>
            res.headers.put("Content-Type", contentType(config.charset))
            res.end(makeError(findId(text), InternalError, t).toString)
            Nil
        }
        
        requests.foreach { r =>
          
          r.params match {
            case param: JsonArray =>
              ebus.post(r.method, param) { reply: Message[JsonArray] =>
                res.headers.put("Content-Type", contentType(config.charset))
                res.end(makeResult(r.id, reply.body).toString)
              }
            case param: JsonObject =>
              ebus.post(r.method, param) { reply: Message[JsonObject] =>
                res.headers.put("Content-Type", contentType(config.charset))
                res.end(makeResult(r.id, reply.body).toString)
              }
            case null =>
              ebus.post(r.method) { reply: Message[JsonObject] =>
                res.headers.put("Content-Type", contentType(config.charset))
                res.end(makeResult(r.id, reply.body).toString)
              }
          }
          
          
        }
        
        //println(body.toString)
        
        val json = new JsonObject(body.toString)
        
        println(json.toString)
        
        var qux = json.getArray("qux")
        
        var l = List("a","b","c")
        val v = l(0)
      }
      
      
      
      res.headers.put("Content-Type", s"application/json; charset=${config.charset}")
      res.end()
      
    } .listen(config.port)
    
  }
  
  override def stop(): Unit = {
    container.getLogger.info(s"${getClass.getName} stop")
  }
  
}
