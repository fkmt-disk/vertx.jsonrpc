package jsonrpc.core

import org.vertx.java.core.json.DecodeException
import org.vertx.java.core.json.JsonObject

import JsonRpcError.InternalError
import JsonRpcError.InvalidRequest
import JsonRpcError.ParseError

/**
 * JsonRpcRequest.
 * 
 * @author fkmt.disk@gmail.com
 */
class JsonRpcRequest private(json: JsonObject) {
  
  def this(text: String) = this(
    try {
      new JsonObject(text)
    }
    catch {
      case e: DecodeException =>
        throw new JsonRpcException(ParseError, e.getMessage, e)
      case e: Throwable =>
        throw new JsonRpcException(InternalError, e.getMessage, e)
    }
  )
  
  def this(map: java.util.Map[String, Object]) = this(new JsonObject(map))
  
  val jsonrpc = json.getString("jsonrpc") match {
    case null =>
      throw new JsonRpcException(InvalidRequest, "`jsonrpc` is required")
    case version: String if version == "2.0" =>
      version
    case what =>
      throw new JsonRpcException(InvalidRequest, s"unsupported jsonrpc version: ${what}")
  }
  
  val method = json.getString("method") match {
    case null =>
      throw new JsonRpcException(InvalidRequest, "`method` is required")
    case method: String if method.length == 0 =>
      throw new JsonRpcException(InvalidRequest, "`method` is required")
    case method: String if method.length > 0 =>
      method
    case what =>
      sys.error(s"unkown method: ${what}")
  }
  
  val params = json.getElement("params")
  
  val id: Option[AnyRef] = json.getField("id") match {
    case null =>
      if (json.getFieldNames.contains("id"))
        throw new JsonRpcException(InvalidRequest, "not allowed the `id` of null")
      None
    case id: String =>
      Option(id)
    case id: Integer =>
      Option(id)
    case id: Number =>
      throw new JsonRpcException(InvalidRequest, "`id` must be string or integer")
  }
  
}

object JsonRpcRequest {
  
  def parse(text: String) = new JsonRpcRequest(text)
  
  def findId(text: String): Option[AnyRef] = try {
    new JsonObject(text).getField("id") match {
      case null => None
      case id   => Option(id)
    }
  }
  catch {
    case t: Throwable => None
  }
  
}
