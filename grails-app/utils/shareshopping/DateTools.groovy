package shareshopping

import java.text.SimpleDateFormat

class DateTools {
    static sdf=new SimpleDateFormat("yyyy-MM-dd")
    static sdf1=new SimpleDateFormat("yyyyMMdd")
    static sdf2=new SimpleDateFormat("yyyy.MM.dd")
    static parseDate(dateStr){
        if(!dateStr){
            return null
        }
        return sdf.parse(dateStr)
    }
    static formatDate(date){
        if(!date){
            return null
        }
        return sdf.format(date)
    }
    static formatDate2(date){
        if(!date){
            return null
        }
        return sdf2.format(date)
    }
    static dateString(date=null){
        if(date==null||!(date instanceof Date)){
            date=new Date()
        }
        def dateStr= sdf1.format(date)
        return dateStr.replaceFirst("20","")
    }
}
