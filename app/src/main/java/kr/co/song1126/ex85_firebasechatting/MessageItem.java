package kr.co.song1126.ex85_firebasechatting;

public class MessageItem {

    String name;            //닉네임
    String message;         //메세지
    String time;            //작성시간
    String profileUrl;      //프로필이미지 https:// --- URL(스토리지에 저장된주소)

    public MessageItem() {
    }

    public MessageItem(String name, String message, String time, String profileUrl) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.profileUrl = profileUrl;
    }


    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
