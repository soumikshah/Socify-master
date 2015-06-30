package extinguishers.com.socify;

public class Post {
    private String userPhoto, postPhoto;
    private String postUsername, postTitle, postBody, postSource;
    private String postDatetime;
    private boolean visible;

    public Post(String userPhoto, String postPhoto, String postUsername, String postTitle, String postBody, String postSource, String postDatetime) {
        setUserPhoto(userPhoto);
        setPostPhoto(postPhoto);
        setPostUsername(postUsername);
        setPostTitle(postTitle);
        setPostBody(postBody);
        setPostSource(postSource);
        setPostDatetime(postDatetime);
        setVisible(true);
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getPostPhoto() {
        return postPhoto;
    }

    public void setPostPhoto(String postPhoto) {
        this.postPhoto = postPhoto;
    }

    public String getPostUser() {
        return postUsername;
    }

    public void setPostUsername(String postUsername) {
        this.postUsername = postUsername;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public String getPostSource() {
        return postSource;
    }

    public void setPostSource(String postSource) {
        this.postSource = postSource;
    }

    public String getPostDatetime() {
        return postDatetime;
    }

    public void setPostDatetime(String postDatetime) {


        this.postDatetime = postDatetime;
    }
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}