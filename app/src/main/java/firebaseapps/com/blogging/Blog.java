package firebaseapps.com.blogging;

/**
 * Created by 1405214 on 10-09-2016.
 * This class helps to store the data
 * in a object of it's class
 * latter that object can be used to display data
 */
public class Blog {
   private String desc;
    private String imageurl;
    private String title;
    private String User;
    private String profilepic;

    public Blog() {
    }

    public Blog(String desc, String imageurl, String title, String user, String profilepic) {
        this.desc = desc;
        this.imageurl = imageurl;
        this.title = title;
        this.User = user;
        this.profilepic = profilepic;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageurl() {
        return imageurl;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
