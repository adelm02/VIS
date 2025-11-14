package domain;

public class Recepcni {
    public int id;
    public String name;
    public String login;
    public String password;

    public Recepcni(int id, String name, String login, String password) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.password = password;
    }

    public void infoPrint() {
        System.out.println("Recepční: " + name + " | Login: " + login);
    }
}