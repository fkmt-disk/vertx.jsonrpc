package jsonrpc.core

import org.vertx.java.core.json.JsonObject
import org.vertx.java.core.json.DecodeException

import JsonRpcError._

class JsonRequest private(text: String) {
  
  private val json = try {
    new JsonObject(text)
  }
  catch {
    case e: DecodeException =>
      throw new JsonRpcException(ParseError, e.getMessage, e)
    case e: Throwable =>
      throw new JsonRpcException(InternalError, e.getMessage, e)
  }
  
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
  
}

object JsonRequest {
  
  def apply(text: String): List[JsonRequest] = {
    // TODO
    Nil
  }
  
  def typeof(text: String): Symbol = text match {
    case null => 'null
    case s if s.trim().startsWith("{") => 'object
    case s if s.trim().startsWith("[") => 'array
    case _ => 'not_jsonobject
  }
  
}
