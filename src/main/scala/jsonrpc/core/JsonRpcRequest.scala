package jsonrpc.core

import scala.Array.canBuildFrom

import org.vertx.java.core.json.DecodeException
import org.vertx.java.core.json.JsonArray
import org.vertx.java.core.json.JsonObject

import JsonRpcError.InternalError
import JsonRpcError.InvalidRequest
import JsonRpcError.ParseError

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
  
  val id: AnyRef = json.getField("id") match {
    case null =>
      if (json.getFieldNames.contains("id"))
        throw new JsonRpcException(InvalidRequest, "not allowed the `id` of null")
      null
    case id: String =>
      id
    case id: Integer =>
      id
    case id: Number =>
      throw new JsonRpcException(InvalidRequest, "`id` must be string or integer")
  }
  
}

object JsonRpcRequest {
  
  type jmap = java.util.Map[String, Object]
  
  def parse(text: String): List[JsonRpcRequest] = text match {
    case null =>
      throw new JsonRpcException(ParseError, "no content")
    case text if text.trim.length == 0 =>
      throw new JsonRpcException(ParseError, "no content")
    case text if text.trim.startsWith("{") =>
      List(new JsonRpcRequest(text))
    case text if text.trim.startsWith("[") =>
      new JsonArray(text).toArray.collect {
        case map: jmap =>
          new JsonRpcRequest(map)
        case what =>
          throw new JsonRpcException(ParseError, s"unsupported type: ${what}")
      } .toList
      
    case what =>
      throw new JsonRpcException(ParseError, s"unsupported type: ${what}")
  }
  
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
