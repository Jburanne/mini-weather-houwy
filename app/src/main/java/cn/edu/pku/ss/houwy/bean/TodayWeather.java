package cn.edu.pku.ss.houwy.bean;

public class TodayWeather {
    private String city;
    private String updatetime;
    private String wendu;
    private String shidu;
    private String pm25;
    private String quality;
    private String fengxiang;
    private String fengli;
    private String date;
    private String high;
    private String low;
    private String type;

    public String getCity() {
        return city;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public String getWendu() {
        return wendu;
    }

    public String getQuality(){
        return quality;
    }

    public  String getDate(){
        return date;
    }

    public  String getLow(){
        return low;
    }

    public String getHigh(){
        return high;
    }

    public  String getFengli(){
        return fengli;
    }

    public String getPm25() {
        return pm25;
    }

    public String getShidu() {
        return shidu;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setCity(String city){
        this.city = city;
    }

    public void setUpdatetime(String updatetime){
        this.updatetime = updatetime;
    }

    public void setShidu(String shidu){
        this.shidu = shidu;
    }

    public void setWendu(String wendu){
        this.wendu = wendu;
    }

    public void setPm25(String pm25){
        this.pm25 = pm25;
    }

    public void setQuality(String quality){
        this.quality = quality;
    }

    public void setFengxiang(String fengxiang){
        this.fengxiang = fengxiang;
    }

    public void setFengli(String fengli){
        this.fengli = fengli;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override

    public String toString(){
        return "TodayWeather{"+"city='"+city+'\''+",updatetime='"+updatetime+'\''
                +",wendu='"+wendu+'\''
                +",shidu='"+shidu+'\''
                +",pm25='"+pm25+'\''
                +",quality='"+quality+'\''
                +",fengxiang='"+fengxiang+'\''
                +",fengli='"+fengli+'\''
                +",date='"+date+'\''
                +",high='"+high+'\''
                +",low='"+low+'\''
                +",type='"+type+'\''+'}';

    }
}
