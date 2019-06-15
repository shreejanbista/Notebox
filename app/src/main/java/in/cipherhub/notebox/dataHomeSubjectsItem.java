package in.cipherhub.notebox;

public class dataHomeSubjectsItem {

    String subAbb, subName, lastUpdate;
    Boolean subBookmark;

    dataHomeSubjectsItem(String subAbb, String subName, String lastUpdate, Boolean subBookmark) {
        this.subAbb = subAbb;
        this.subName = subName;
        this.lastUpdate = lastUpdate;
        this.subBookmark = subBookmark;
    }
}
