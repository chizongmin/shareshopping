package shareshopping

interface Code {
    int goodsDelete= 1101   //商品下架
    int goodsEmpty= 1102   //商品库存不足
    int addressDisable= 1103   //地址不支持配送
    int goodsNatureDiff= 1104   //商品属性不相同
    int scoreNotEnough=1201 //积分不足
    //优惠券
    int couponNotFound=1211 //未发现可用优惠券
    int couponGtSum=1212 //优惠券金额大于商品金额
    //订单状态
    int orderStatusChangeError=1301 //订单状态流转错误
    //标签
    int tagRepeat=1401 //标签重复
    int serviceStop=2001 //服务暂停
}
