package info.androidhive.materialdesign.adapter;

/**
 * Created by HP ENVY on 5/18/2017.
 */


public class notifications {
    private String  content,category,link,  posted_by, imagelink;
    private int height = 0, id, date, read;
    private boolean expanded = false;

    public notifications() {
    }


    public boolean getExpanded(){
        return expanded;
    }

    public String getContent(){
        return content;
    }
    public int getId(){
        return id;
    }

    public int getHeight(){
        return height;
    }

    public String getCategory(){
        return category;
    }

    public String getLink(){
        return link;
    }

    public int getDate(){
        return date;
    }

    public int getRead(){
        return read;
    }

    public String getImageLink(){
            return imagelink;
    }

    public String getPostedBy(){
        return posted_by;
    }

    public void setContent(String name){
        content = name;
    }

    public void setCategory(String name){
        category = name;
    }

    public void setLink(String name){
        link = name;
    }

    public void setDate(int name){
        date = name;
    }

    public void setHeight(int h){
        height = h;
    }
    public void setRead(int name){
        read = name;
    }

    public void setId(int id){
        this.id = id;
    }
    public void setImageLink(String name){
        imagelink = name;
    }

    public void setPostedBy(String name){
        posted_by = name;
    }

    public void setExpanded(boolean ex){
        expanded = ex;
    }

}