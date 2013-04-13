package jsonrpc.core

import scala.Array.canBuildFrom

import org.vertx.java.core.json.JsonObject

import jsonrpc.core.JsonConverter.JavaMapToJson

/**
 * Config.
 * 
 * @author fkmt.disk@gmail.com
 */
class Config(json: JsonObject) {
  
  private type TypedJavaMap = java.util.Map[String, AnyRef]
  
  val port = json.getInteger("port")
  
  val charset = json.getString("charset")
  
  val workers = json.getArray("workers").toArray.collect {
    case map: TypedJavaMap => new WorkerConfig(map.toJson)
  }
  
}

/**
 * WorkerConfig.
 * 
 * @author fkmt.disk@gmail.com
 */
class WorkerConfig(json: JsonObject) {
  
  val className = json.getString("class_name")
  
  val config = json.getObject("config")
  
  val instances: Int = json.getInteger("instances") match {
    case null       => 1
    case x: Integer => x
  }
  
  val name = config match {
    case null             => "noname"
    case json: JsonObject => json.getString("name", "noname")
  }
  
}
