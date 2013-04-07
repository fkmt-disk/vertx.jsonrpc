package jsonrpc.core

import collection.JavaConversions._

import org.vertx.java.core.json.JsonObject

object Test {
  
  def main(args: Array[String]): Unit = {
    println("!")
    
    val json = new JsonObject("""{
        "hoge": "string",
        "fuga": 123,
        "piyo": false,
        "foo": {
          "aaa": "aaa",
          "bbb": 456,
          "ccc": {
            "a": [1,2,3],
            "b": {
              "x": "stringstring",
              "y": 789,
              "z": true
            }
          }
        }
    }""")
    
    val map: Map[String, AnyRef] = json.toMap.toMap
    
    /*
    val map = Map("aaa"->123, "bbb"->"hoge")
    val l = map.collect {
      case (x:String, y:Any) => (null, null)
    }*/
    
    
  }
  
  class Klass(json: JsonObject) {
    
    
  }
  
  
  
}
