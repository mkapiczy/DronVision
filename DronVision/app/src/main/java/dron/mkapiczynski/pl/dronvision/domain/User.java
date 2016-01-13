package dron.mkapiczynski.pl.dronvision.domain;

/**
 * Created by Miix on 2015-11-07.
 */
public class User {

    private String login;

    public User() {
    }

    public User(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
