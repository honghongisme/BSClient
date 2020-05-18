package bean

data class UserPostData<T>(val endIndex: Int,
                           var postData: Array<T>,
                           val countData : CountData
                           )