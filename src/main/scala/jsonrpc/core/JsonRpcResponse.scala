package jsonrpc.core

import scala.collection.mutable.{Map => MutableMap}

import org.vertx.java.core.json.JsonElement
import org.vertx.java.core.json.JsonObject

import jsonrpc.core.JsonConverter.MutableMapToJson

object JsonRpcResponse {
  
  def contentType(charset: String) = s"application/json; charset=${charset}"
  
  private def pass = Unit
  
  private def mkBase(id: Option[AnyRef]): MutableMap[String, Any] = {
    val res: MutableMap[String, Any] = MutableMap()
    
    res += "jsonrpc" -> "2.0"
    
    id match {
      case Some(id) => res += "id" -> id
      case None     => pass
    }
    
    res
  }
  
  def makeResult(id: Option[AnyRef], result: JsonElement): JsonObject = {
    val base = mkBase(id)
    
    if (result.isArray)
      base += "result" -> result.asArray.toArray
    else
      base += "result" -> result.asObject.toMap
    
    base.toJson
  }
  
  def makeError(id: Option[AnyRef], error: JsonObject): JsonObject = {
    val base = mkBase(id)
    base += "error" -> error.toMap
    base.toJson
  }
  
  def makeError(id: Option[AnyRef], error: JsonRpcError, cause: Throwable=null): JsonObject = makeError(id, new JsonRpcException(error, cause).toJson)
  
}