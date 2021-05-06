package order

import mongo.MongoService


class OrderActivityService extends MongoService{
    OrderService orderService
    OrderActivityTemplateService orderActivityTemplateService
    @Override
    String collectionName() {
        "orderActivity"
    }
    def addActivity(orderId){
        def order=orderService.findById(orderId)
        this.save([orderId:orderId,status:order.status,strStatus:order.strStatus])
    }
    def orderActivity(orderId){
        def list=[]
        def activity=this.findAll([orderId:orderId],[dateCreated:-1])
        def statusContentMap=orderActivityTemplateService.statusContentMap()
        activity.each{item->
            item.content=statusContentMap[item.status]
            list.add(item)
        }
        return list
    }
}
