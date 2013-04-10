package jsonrpc.core

class JsonRpcException(
    _error: JsonRpcError
  , _message: String
  , _cause: Throwable
) extends RuntimeException {
  
  def this(_error: JsonRpcError) = this(_error, null, null)
  
  def this(_error: JsonRpcError, _message: String) = this(_error, _message, null)
  
  def this(_error: JsonRpcError, _cause: Throwable) = this(_error, null, _cause)
  
  val error = _error
  
  val message = _message
  
  val cause = _cause
  
}