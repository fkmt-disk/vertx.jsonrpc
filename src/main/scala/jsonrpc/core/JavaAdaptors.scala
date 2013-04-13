package jsonrpc.core

import java.util.{List => JavaList}
import java.util.{Map => JavaMap}

import scala.collection.JavaConversions.mapAsJavaMap
import scala.collection.JavaConversions.seqAsJavaList
import scala.collection.mutable.{ListBuffer => MutableList}
import scala.collection.mutable.{Map => MutableMap}

object JavaAdaptors {
  
  implicit def bool2ref(b: Boolean) = b.asInstanceOf[java.lang.Boolean]
  
  implicit def byte2ref(b: Byte) = b.asInstanceOf[java.lang.Byte]
  
  implicit def short2ref(s: Short) = s.asInstanceOf[java.lang.Short]
  
  implicit def int2ref(i: Int) = i.asInstanceOf[Integer]
  
  implicit def long2ref(l: Long) = l.asInstanceOf[java.lang.Long]
  
  implicit def float2ref(f: Float) = f.asInstanceOf[java.lang.Float]
  
  implicit def double2ref(d: Double) = d.asInstanceOf[java.lang.Double]
  
  implicit def char2ref(c: Char) = c.asInstanceOf[java.lang.Character]
  
  
  private type TypedMap = Map[String, Any]
  private type TypedMutableMap = MutableMap[String, Any]
  
  private type TypedList = List[Any]
  private type TypedMutableList = MutableList[Any]
  
  private type TypedJavaMap = JavaMap[String, Any]
  private type TypedJavaList = JavaList[Any]
  
  
  implicit def any2ref(map: TypedMap) = map.asInstanceOf[Map[String, AnyRef]]
  
  implicit def any2ref(map: TypedMutableMap) = map.asInstanceOf[MutableMap[String, AnyRef]]
  
  implicit def any2ref(map: TypedJavaMap) = map.asInstanceOf[JavaMap[String, AnyRef]]
  
  
  implicit def any2ref(list: TypedList) = list.asInstanceOf[List[AnyRef]]
  
  implicit def any2ref(list: TypedMutableList) = list.asInstanceOf[MutableList[AnyRef]]
  
  implicit def any2ref(list: TypedJavaList) = list.asInstanceOf[JavaList[AnyRef]]
  
  
  implicit class ImmutableMapConverter(map: TypedMap) {
    def toJava: JavaMap[String, AnyRef] = convMap(map)
  }
  
  implicit class MutableMapConverter(map: TypedMutableMap) {
    // implicit classで補助コンストラクタ作っても有効にならない
    // SIP-13: http://docs.scala-lang.org/sips/pending/implicit-classes.html
    def toJava: JavaMap[String, AnyRef] = convMap(map)
  }
  
  
  implicit class ImmutableListConverter(list: TypedList) {
    def toJava: JavaList[AnyRef] = convList(list)
  }
  
  implicit class MutableListConverter(list: TypedMutableList) {
    def toJava: JavaList[AnyRef] = convList(list)
  }
  
  
  private def convMap(map: TypedMap): TypedJavaMap = {
    map.collect {
      case (k: String, v: TypedMap) =>
        k -> convMap(v)
      case (k: String, v: TypedMutableMap) =>
        k -> convMap(v)
      case (k: String, v: TypedList) =>
        k -> convList(v)
      case (k: String, v: TypedMutableList) =>
        k -> convList(v)
      case x =>
        x
    }
  }
  
  private def convMap(map: TypedMutableMap): TypedJavaMap = convMap(map.toMap)
  
  private def convList(list: TypedList): TypedJavaList = {
    list.collect {
      case x: TypedMap =>
        convMap(x)
      case x: TypedMutableMap =>
        convMap(x)
      case x: TypedList =>
        convList(x)
      case x: TypedMutableList =>
        convList(x)
      case x =>
        x
    }
  }
  
  private def convList(list: TypedMutableList): TypedJavaList = convList(list.toList)
  
}