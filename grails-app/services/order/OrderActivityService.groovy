package order

import mongo.MongoService
import user.UserService


class OrderActivityService extends MongoService{
    OrderService orderService
    OrderActivityTemplateService orderActivityTemplateService
    UserService userService
    @Override
    String collectionName() {
        "orderActivity"
    }
    def addActivity(operatorToken,orderId){
        def order=orderService.findById(orderId)
        def toSave=[orderId:orderId,status:order.status,strStatus:order.strStatus]
        toSave.operatorToken=operatorToken
        toSave.operatorName="系统"
        if(operatorToken){
            def operator=userService.findOne([token:operatorToken])
            toSave.operatorName=operator?.name
        }
        this.save(toSave)
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
