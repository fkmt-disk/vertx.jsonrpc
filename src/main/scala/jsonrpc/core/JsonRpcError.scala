package jsonrpc.core

/**
 * JsonRpcError.
 * 
 * @author fkmt.disk@gmail.com
 */
object JsonRpcError {
  
  /**
   * ParseError.
   * code : -32700
   * mean : Invalid JSON was received by the server.
   *        An error occurred on the server while parsing the JSON text.
   */
  case object ParseError extends JsonRpcError(-32700, "ParseError")
  
  /**
   * InvalidRequest.
   * code : -32600
   * mean : The JSON sent is not a valid Request object.
   */
  case object InvalidRequest extends JsonRpcError(-32600, "InvalidRequest")
  
  /**
   * MethodNotFound.
   * code : -32601
   * mean : The method does not exist / is not available.
   */
  case object MethodNotFound extends JsonRpcError(-32601, "MethodNotFound")
  
  /**
   * InvalidParams.
   * code : -32602
   * mean : Invalid method parameter(s).
   */
  case object InvalidParams extends JsonRpcError(-32602, "InvalidParams")
  
  /**
   * InternalError.
   * code : -32603
   * mean : Internal JSON-RPC error.
   */
  case object InternalError extends JsonRpcError(-32603, "InternalError")
  
}

sealed abstract class JsonRpcError(
    val code: Integer
  , val name: String
)
