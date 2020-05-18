package com.example.picturerecognize.entity.view.recognize

// baidu ocr api只能分析一行 所以这个Array数组实际只有第一个元素有用（或者只有一个元素）
class VatInvoiceResult (var CommodityName : Array<VatInvoiceResultCommodity>, // 货物名称
                        var CommodityAmount : Array<VatInvoiceResultCommodity>, // 金额
                        var CommodityTax : Array<VatInvoiceResultCommodity>, // 税额
                        var InvoiceDate : String // 开票日期


//                             var SellerName : String, // 销售方名称
//                             var SellerBank : String, // 销售方开户行及账号
//                             var Checker : String, // 复核
//                             var TotalAmount: String, // 合计金额
//                             var CommodityTaxRate : Array<VATPicRespCommodity>, // 税率
//                             var PurchaserName : String, // 购方名称
//                             var CommodityNum : List<VATPicRespCommodity>, // 数量
//                             var PurchaserBank : String, // 购方开户行及账号
//                             var Remarks : String, // 备注
//                             var Password : String, // 密码区
//                             var SellerAddress : String, // 销售方地址及电话
//                             var PurchaserAddres : String, // 购方地址及电话
//                             var InvoiceCode : String, // 发票代码
//                             var CommodityUnit : Array<VATPicRespCommodity>, // 单位
//                             var Payee : String, // 收款⼈
//                             var PurchaserRegisterNum : String, // 购方纳税人识别号
//                             var CommodityPrice : Array<VATPicRespCommodity>, // 单价
//                             var NoteDrawer : String, // 开票人
//                             var AmountInWords : String, // 价税合计(大写)
//                             var AmountInFiguers : String, // 价税合计(小写)
//                             var TotalTax : String, // 合计税额
//                             var InvoiceType : String, // 发票种类
//                             var SellerRegisterNum: String, // 销售方纳税人识别号
//                             var CommodityType : Array<VATPicRespCommodity>, // 规格型号
//                             var InvoiceTypeOrg : String,  // 发票名称
//                             var InvoiceNum : String
)