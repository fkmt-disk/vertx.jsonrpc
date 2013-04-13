package jsonrpc.core

import java.util.{List => JavaList}
import java.util.{Map => JavaMap}

import scala.collection.mutable.{ListBuffer => MutableList}
import scala.collection.mutable.{Map => MutableMap}

import org.vertx.java.core.json.JsonArray
import org.vertx.java.core.json.JsonObject

import jsonrpc.core.JavaAdaptors.ImmutableListConverter
import jsonrpc.core.JavaAdaptors.ImmutableMapConverter
import jsonrpc.core.JavaAdaptors.MutableListConverter
import jsonrpc.core.JavaAdaptors.MutableMapConverter

object JsonConverter {
  
  implicit class ImmutableMapToJson(map: Map[String, Any]) {
    def toJson = new JsonObject(map.toJava)
  }
  
  implicit class MutableMapToJson(map: MutableMap[String, Any]) {
    def toJson = new JsonObject(map.toJava)
  }
  
  implicit class JavaMapToJson(map: JavaMap[String, AnyRef]) {
    def toJson = new JsonObject(map)
  }
  
  implicit class ImmutableListToJson(list: List[Any]) {
    def toJson = new JsonArray(list.toJava)
  }
  
  implicit class MutableListToJson(list: MutableList[Any]) {
    def toJson = new JsonArray(list.toJava)
  }
  
  implicit class JavaListToJson(list: JavaList[AnyRef]) {
    def toJson = new JsonArray(list)
  }
  
}
