package jsonrpc.core

/**
 * JsonConverter.
 * 
 * @author fkmt.disk@gmail.com
 */
object JsonConverter {
  
  import org.vertx.java.core.json.JsonObject
  import java.util.{ Map => JMap }
  
  def obj2json(obj: AnyRef): JsonObject = jconv(obj) match {
    case map: JMap[_, _] => new JsonObject( map.asInstanceOf[JMap[String, AnyRef]] )
    case any             => throw new IllegalArgumentException(s"unsupported type: ${any.getClass}")
  }
  
  private def jconv(arg: Any): AnyRef = arg match {
    case x: Char       => x.toString
    case x: String     => x
    case x: Number     => x
    case x: Boolean    => x.asInstanceOf[java.lang.Boolean]
    case x: Symbol     => x.name
    case x: Seq[_]     => x.toJavaList
    case x: Map[_, _]  => x.toJavaMap
    case x: AnyRef     => x.toValMap.toJavaMap
    case x             => throw new IllegalArgumentException(s"unsupported type: ${x.getClass}")
  }
  
  import scala.language.implicitConversions
  
  implicit private class SeqAsJava(seq: Seq[_]) {
    import scala.collection.JavaConversions.seqAsJavaList
    
    def toJavaList = {
      val list = seq.map(jconv)
      seqAsJavaList(list)
    }
  }
  
  implicit private class MapAsJava(map: Map[_, _]) {
    import scala.collection.JavaConversions.mapAsJavaMap
    
    def toJavaMap = {
      val dist = map.map {
        case (key: Symbol, value) => (key.name, jconv(value))
        case (key, value)         => (key.toString, jconv(value))
      }
      mapAsJavaMap(dist)
    }
  }
  
  implicit private class ClassToMap(instance: AnyRef) {
    val klass = instance.getClass
    
    def toValMap(): Map[String, AnyRef] = {
      klass.getDeclaredFields.map(_.getName).map { name =>
        (name, klass.getDeclaredMethod(name).invoke(instance))
      } .toMap
    }
  }
  
  
  
  /*
   * for test
   */
  def main(args: Array[String]): Unit = {
    class Fuga {
      val mojiretsu = "moji"
      val suuji = new java.math.BigDecimal(123)
      val list = List('a', 'b', 'c')
    }
    
    class Hoge {
      val foo = "moji"
      val bar = 123
      val qux = false
      val list1 = List(9, 8, 7)
      val list2 = List("A", "B", "C")
      val list3 = List(new Fuga, new Fuga, new Fuga)
      val map1 = Map("key1"->"a", "key2"->"b", "key"->"c")
      val map2 = Map('name1->List(1,2,3), 'name2->List(4,5,6), 'name3->List(7,8,9))
      val obj = new Fuga
    }
    
    println(JsonConverter.obj2json(new Hoge).toString)
  }
  
  import scala.reflect.ClassTag
  import scala.language.reflectiveCalls
  
  abstract class Request[T <: {def load(json :JsonObject): T}](json: JsonObject)(implicit val tag: ClassTag[T]) {
    
    val jsonrpc = json.getString("jsonrpc")
    
    val method = json.getString("method")
    
    val id = json.getLong("id")
    
    val param: T = tag.runtimeClass.asInstanceOf[Class[T]].newInstance().load(json)
    
  }
  
}
