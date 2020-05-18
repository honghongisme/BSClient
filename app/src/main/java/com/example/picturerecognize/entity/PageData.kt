package bean.post

data class PageData<T> (val endIndex : Int,
                     var data : Array<T>
)