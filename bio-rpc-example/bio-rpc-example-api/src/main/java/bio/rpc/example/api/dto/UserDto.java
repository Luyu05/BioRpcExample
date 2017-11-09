package bio.rpc.example.api.dto;

import java.io.Serializable;

/**
 * Created by luyu on 2017/11/9.
 */
public class UserDto implements Serializable{

    private static final long serialVersionUID = 1L;

    private String userName;
    private String word;

    public UserDto(String userName, String word) {
        this.userName = userName;
        this.word = word;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "userName='" + userName + '\'' +
                ", word='" + word + '\'' +
                '}';
    }

}
