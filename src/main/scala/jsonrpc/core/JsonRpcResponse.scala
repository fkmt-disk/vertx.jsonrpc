package jsonrpc.core

import scala.collection.mutable.{Map => MutableMap}

import org.vertx.java.core.json.JsonElement
import org.vertx.java.core.json.JsonObject

import jsonrpc.core.JsonConverter.MutableMapToJson

object JsonRpcResponse {
  
  def contentType(charset: String) = s"application/json; charset=${charset}"
  
  private def mkBase(id: AnyRef = null): JsonObject = {
    val res: MutableMap[String, Any] = MutableMap()
    
    res += "jsonrpc" -> "2.0"
    
    if (id != null)
      res += "id" -> id
    
    res.toJson
  }
  
  def makeResult(id: AnyRef, result: JsonElement): JsonObject = {
    val json = mkBase(id)
    
    if (result.isArray)
      json.putArray("result", result.asArray)
    else
      json.putObject("result", result.asObject)
    
    json
  }
  
  def makeError(id: AnyRef, error: JsonObject): JsonObject = {
    val json = mkBase(id)
    json.putObject("error", error)
    json
  }
  
  def makeError(id: AnyRef, error: JsonRpcError, cause: Throwable=null): JsonObject = {
    val json = mkBase(id)
    json.putObject("error", new JsonRpcException(error, cause).toJson)
    json
  }
  
}