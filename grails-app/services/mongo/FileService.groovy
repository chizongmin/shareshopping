package mongo

import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest

import java.nio.file.Paths
import java.text.SimpleDateFormat

class FileService extends MongoService{
    @Override
    String collectionName() {
        "file"
    }
    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmssS");
    def upload(map){
        def file=map.file
        String dateString=  sdf.format(new Date())
        String  fileProName=file.getOriginalFilename()
        String extension = fileProName.split('\\.')[-1]//截取获取文//件名的后缀
        def fileName=dateString+"."+extension
        def gridFile = saveFile(file.inputStream, fileName,"file")
        return  [id:gridFile.objectId.toString()]    //返回文件名称
    }
}
