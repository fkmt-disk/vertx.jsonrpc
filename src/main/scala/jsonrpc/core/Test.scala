package jsonrpc.core

import collection.JavaConversions._
import org.vertx.java.core.json.JsonObject
import org.vertx.java.core.json.JsonArray

object Test {
  
  def main(args: Array[String]): Unit = {
    
    //--------------------------------------------------------------------------
    // org.vertx.java.core.json.DecodeException:
    //  Failed to decode:null
    /*
    val j1 = new JsonObject(null.asInstanceOf[String])
    println(j1)
    */
    
    
    //--------------------------------------------------------------------------
    // org.vertx.java.core.json.DecodeException:
    //  Failed to decode:No content to map to Object due to end of input
    /*
    val j2 = new JsonObject("")
    println(j2)
    */
    
    
    val j3 = new JsonObject("{}")
    println(j3)
    
    
    //--------------------------------------------------------------------------
    // org.vertx.java.core.json.DecodeException:
    //  Failed to decode:Can not deserialize instance of java.util.LinkedHashMap out of START_ARRAY token
    /*
    val j4 = new JsonObject("[]")
    println(j4)
    */
    
    
    val j5 = new JsonObject("""{"hoge":[1,2,3]}""")
    println(j5)
    
    
    val j6 = new JsonArray("[1,2,3]")
    println(j6)
    
    val j7 = new JsonArray("""[[1,2,3],{"a":1, "b":2, "c":3},{"d":4, "e":5, "f":6}]""")
    println(j7)
    val j7array = j7.toArray
    println(j7array(0).getClass.getName)
    
    
    val a1 = new JsonObject("""{"array":[1,2,3]}""").getElement("array")
    println(a1.isInstanceOf[JsonArray])
    
    val a2 = new JsonObject("""{"array":{"one":1,"two":2}}""").getElement("array")
    println(a2.isInstanceOf[JsonArray])
    
    val f1 = new JsonObject("""{"id1":123.456, "id2":123, "id3":"abcdef"}""")
    println(f1.getField("id0"))
    println(f1.getField("id1").getClass.getName)
    println(f1.getField("id2").getClass.getName)
    println(f1.getField("id3").getClass.getName)
    
    
  }
  
}
