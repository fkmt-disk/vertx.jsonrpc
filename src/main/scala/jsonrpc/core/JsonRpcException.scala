package jsonrpc.core

import scala.collection.mutable.{Map => MutableMap}

import org.vertx.java.core.json.JsonObject

import JsonConverter.MutableMapToJson

/**
 * JsonRpcException.
 * 
 * @author fkmt.disk@gmail.com
 */
class JsonRpcException(
    _error: JsonRpcError
  , _message: String
  , _cause: Throwable
) extends RuntimeException {
  
  def this(_error: JsonRpcError) = this(_error, null, null)
  
  def this(_error: JsonRpcError, _message: String) = this(_error, _message, null)
  
  def this(_error: JsonRpcError, _cause: Throwable) = this(_error, null, _cause)
  
  
  val error = _error
  
  val message = _message
  
  val cause = _cause
  
  
  def toJson: JsonObject = {
    
    val map: MutableMap[String, Any] = MutableMap(
      "code" -> error.code,
      "message" -> error.name
    )
    
    if (message != null)
      map += "data" -> message
    
    map.toJson
  }
  
}
