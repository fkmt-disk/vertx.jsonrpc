package jsonrpc.core

import org.vertx.java.core.json.JsonObject
import scala.collection.JavaConversions._

class Config(json: JsonObject) {
  
  val port = json.getInteger("port")
  
  val charset = json.getString("charset")
  
  val workers = {
    val workers = json.getObject("workers")
    workers.getFieldNames.map { name =>
      new WorkerConfig(workers.getObject(name))
    }.toList
  }
  
}

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
