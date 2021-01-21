package background.file

import org.bson.types.ObjectId
import shareshopping.BaseController
import org.apache.commons.io.IOUtils
class FileController extends BaseController{
    def fileService
    def upload() {
        def data = fileService.upload(params)
        rv(data)
    }
    def preview() {
        def id=params.id
        response.contentType = "image/*"
        IOUtils.copy(fileService.openFileDownloadStream("file", new ObjectId(id)), response.getOutputStream())
    }
}
