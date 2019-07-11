package in.cipherhub.notebox.Models;

public class ItemSubjectsModel {

    private String subjectName;
    private String subjectAbbColor;

    public ItemSubjectsModel(String subjectName, String subjectAbbColor) {
        this.subjectName = subjectName;
        this.subjectAbbColor = subjectAbbColor;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectAbbColor() {
        return subjectAbbColor;
    }

    public void setSubjectAbbColor(String subjectAbbColor) {
        this.subjectAbbColor = subjectAbbColor;
    }
}
