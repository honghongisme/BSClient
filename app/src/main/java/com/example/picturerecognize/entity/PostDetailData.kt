package bean

// 具体的帖子
data class PostDetailData<T>(val endIndex: Int,
                             var data: Array<T>,
                             val isCollected : Boolean // 是否被收藏过
                           )