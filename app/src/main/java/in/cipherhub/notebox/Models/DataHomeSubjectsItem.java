package in.cipherhub.notebox.Models;

public class DataHomeSubjectsItem {

    public String subAbb, subName, lastUpdate;
    public Boolean subBookmark;

    public DataHomeSubjectsItem(String subAbb, String subName, String lastUpdate, Boolean subBookmark) {
        this.subAbb = subAbb;
        this.subName = subName;
        this.lastUpdate = lastUpdate;
        this.subBookmark = subBookmark;
    }
}
