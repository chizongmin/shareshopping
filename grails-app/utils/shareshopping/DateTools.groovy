package shareshopping

import java.text.SimpleDateFormat

class DateTools {
    static sdf=new SimpleDateFormat("yyyy-MM-dd")
    static sdf1=new SimpleDateFormat("yyyyMMdd")
    static parseDate(dateStr){
        if(!dateStr){
            return null
        }
        return sdf.parse(dateStr)
    }
    static dateString(date=null){
        if(date==null||!(date instanceof Date)){
            date=new Date()
        }
        return sdf1.format(date)
    }
}
