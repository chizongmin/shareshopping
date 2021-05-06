package mongo

class ConfigService extends MongoService{

    @Override
    String collectionName() {
        "config"
    }
}
