package bean

/**
 * {"result":"ok","msg":"登录成功！","data":{"id":1,"budgets":[],"records":[]}}
 */
class Response<T> () {

    var result : String? = null
    var msg : String? = null
    var data : T? = null

}