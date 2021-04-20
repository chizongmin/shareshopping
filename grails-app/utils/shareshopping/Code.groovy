package shareshopping

interface Code {
    int goodsDelete= 1101   //商品下架
    int goodsEmpty= 1102   //商品库存不足
    int scoreNotEnough=1201 //积分不足
    int couponNotFound=1211 //未发现可用优惠券
    int couponGtSum=1212 //优惠券金额大于商品金额
}
