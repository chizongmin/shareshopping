package order

import com.mongodb.client.model.ReturnDocument
import mongo.MongoService
import shareshopping.DateTools

class OrderNumberService extends MongoService{

    @Override
    String collectionName() {
        "orderNumber"
    }
    def created(){
        def dateStr=DateTools.dateString()
        def randomNumber=((int)(Math.random()*900)+100).toString()
        def  data=mongoCollection.findOneAndUpdate([_id:dateStr],
                [$set:[lastUpdated:new Date()], $inc:[sequecce:1]],
                ['upsert': true, 'returnDocument': ReturnDocument.AFTER])
        return dateStr+randomNumber+data.sequecce
    }
}
