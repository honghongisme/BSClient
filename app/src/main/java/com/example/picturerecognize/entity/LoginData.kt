package bean.login


import com.example.picturerecognize.entity.bean.Budget
import com.example.picturerecognize.entity.bean.Record

data class LoginData(var id : Int,
                     var budgets: MutableList<Budget>,
                     var records: MutableList<Record>)