package shareshopping

import java.text.SimpleDateFormat

class DateTools {
    static format=new SimpleDateFormat("yyyy-MM-dd")
    static parseDate(dateStr){
        if(!dateStr){
            return null
        }
        return format.parse(dateStr)
    }
}
