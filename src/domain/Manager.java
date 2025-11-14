package domain;

public class Manager {
    public int id;
    public String name;
    public String login;
    public String password;
    public String position;

    public Manager(int id, String name, String login, String password, String position) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.password = password;
        this.position = position;
    }

    public void changePosition(String newPosition) {
        this.position = newPosition;
        System.out.println(name + " now holds the position: " + newPosition);
    }

    public void infoPrint() {
        System.out.println("Manager: " + name + " | Login: " + login + " | Position: " + position);
    }
}