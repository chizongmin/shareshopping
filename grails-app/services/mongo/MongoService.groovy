package mongo

import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.gridfs.GridFSBucket
import com.mongodb.client.gridfs.GridFSBuckets
import com.mongodb.client.gridfs.model.GridFSFile
import org.bson.types.ObjectId
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import com.mongodb.client.FindIterable
import com.mongodb.client.model.ReturnDocument

abstract class MongoService implements InitializingBean{
    private static final int DEFAULT_BATCH_SIZE = 1000
    @Value('${grails.mongodb.databaseName}')
    String databaseName

    @Autowired
    protected MongoClient mongoClient

    MongoCollection mongoCollection
    abstract String collectionName()
    @Override
    void afterPropertiesSet() {
        mongoCollection = mongoClient.getDatabase(databaseName).getCollection(collectionName())
    }

    GridFSBucket gridFS(bucket) {
        GridFSBuckets.create(mongoClient.getDatabase(databaseName), bucket)
    }
    def findById(id) {
        _idToId(mongoCollection.findOne([_id: id]))
    }

    def findOne(Map filter) {
        idTo_Id(filter)
        _idToId(mongoCollection.findOne(filter))
    }

    def findAll(Map filter) {
        idTo_Id(filter)
        FindIterable result = mongoCollection.find(filter).batchSize(DEFAULT_BATCH_SIZE)
        result.collect { _idToId(it) }
    }

    def findAll(Map filter, Map sort) {
        idTo_Id(filter)
        FindIterable result = mongoCollection.find(filter).sort(sort).batchSize(DEFAULT_BATCH_SIZE)
        result.collect { _idToId(it) }
    }
    def findAll(Map filter, Map sort, List fields, int batchSize = DEFAULT_BATCH_SIZE) {
        idTo_Id(filter)
        def projection = [:]
        fields?.each {
            projection."${it}" = 1
        }
        FindIterable result =mongoCollection.find(filter, projection).sort(sort).batchSize(batchSize)
        result.collect { _idToId(it) }
    }
    def findAll(Map filter,List fields, int batchSize = DEFAULT_BATCH_SIZE) {
        idTo_Id(filter)
        def projection = [:]
        fields?.each {
            projection."${it}" = 1
        }
        FindIterable result =mongoCollection.find(filter, projection).batchSize(batchSize)
        result.collect { _idToId(it) }
    }
    def findAll(Map filter, int offset, int max, Map sort, fields = null) {
        idTo_Id(filter)
        def projection = [:]
        fields?.each {
            projection."${it}" = 1
        }
        long totalCount = count(filter)
        FindIterable result = mongoCollection.find(filter, projection).batchSize(DEFAULT_BATCH_SIZE).skip(offset).limit(max).sort(sort)
        [totalCount: totalCount, items: result.collect { _idToId(it) }]
    }

    def count(Map filter) {
        idTo_Id(filter)
        def result = mongoCollection.aggregate([new BasicDBObject(['$match': filter ?: [:]]), new BasicDBObject(['$group': [_id: null, count: ['$sum': 1]]])])
        if(result.size()){
            return result.first().count
        }else{
            return 0L
        }
    }

    def save(toSave, boolean keepLastUpdated = false) {
        def id = toSave.id
        if (!id) {
            id = new ObjectId().toString()
        }
        toSave._id = id
        toSave.remove("id")
        handleDateCreated(toSave)
        handleLastUpdated(toSave, keepLastUpdated)
        def result = mongoCollection.findOneAndReplace(["_id": id], toSave, ['upsert': true, 'returnDocument': ReturnDocument.AFTER])
        _idToId(result)
    }

    def upsert(filter, toSave, boolean keepLastUpdated = false) {
        idTo_Id(filter)
        def id = toSave.id
        if (!id) {
            id = new ObjectId().toString()
        }
        toSave._id = id
        toSave.remove("id")
        idFilter(filter)
        handleDateCreated(toSave)
        handleLastUpdated(toSave, keepLastUpdated)
        def result = mongoCollection.findOneAndReplace(filter, toSave, ['upsert': true, 'returnDocument': ReturnDocument.AFTER])
        _idToId(result)
    }

    private idFilter(filter) {
        if (!filter._id) {
            def record = mongoCollection.findOne(filter)
            if (record) {
                filter._id = record._id
            }
        }
    }

    def updateById(id, Map update) {
        handleLastUpdated(update)
        def result = mongoCollection.findOneAndUpdate(["_id": id], ['$set': update], ['returnDocument': ReturnDocument.AFTER])
        _idToId(result)
    }

    def updateOne(Map filter, Map update) {
        idTo_Id(filter)
        handleLastUpdated(update)
        idFilter(filter)
        def result = mongoCollection.findOneAndUpdate(filter, ['$set': update], ['returnDocument': ReturnDocument.AFTER])
        _idToId(result)
    }

    def updateBatch(Map filter, Map update) {
        idTo_Id(filter)
        handleLastUpdated(update)
        mongoCollection.updateMany(filter, ['$set': update], ['upsert': false])
    }
    def delete(Map filter) {
        idTo_Id(filter)
        mongoCollection.remove(filter)
    }

    GridFSFile saveFile(File file, String bucket) {
        GridFSBucket fsBucket = gridFS(bucket)
        ObjectId id = fsBucket.uploadFromStream(file.name, new FileInputStream(file))
        fsBucket.find(new BasicDBObject("_id": id)).first()
    }

    GridFSFile saveFile(InputStream is, String fileName, String bucket) {
        GridFSBucket fsBucket = gridFS(bucket)
        ObjectId id = fsBucket.uploadFromStream(fileName, is)
        fsBucket.find(new BasicDBObject("_id": id)).first()
    }

    def deleteFile(String bucket, ObjectId id) {
        gridFS(bucket).delete(id)
    }

    def openFileDownloadStream(String bucket, id) {
        gridFS(bucket).openDownloadStream(id)
    }

    GridFSFile retrieveFile(String bucket, id) {
        GridFSBucket fsBucket = gridFS(bucket)
        fsBucket.find(new BasicDBObject("_id": id)).first()
    }

    private handleDateCreated(data) {
        if (!data.dateCreated) {
            data.dateCreated = Calendar.getInstance().getTime()
        }
    }

    private handleLastUpdated(data, boolean keepOriginal = false) {
        if (keepOriginal && data.lastUpdated) {
            return
        }
        def now = Calendar.getInstance().getTime()
        data.lastUpdated = now
    }

    private def _idToId(doc) {
        if (!doc) {
            return null
        }
        def _id = doc._id
        if (_id) {
            doc.id = _id
        }
        doc.remove("_id")
        doc
    }

    private def idTo_Id(doc) {
        if (!doc) {
            return null
        }
        def id = doc.id
        if (id) {
            doc._id = id
        }
        doc.remove("id")
        doc
    }
}
