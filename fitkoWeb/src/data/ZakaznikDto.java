package data;

public class ZakaznikDto {
    private String password;
    public int id;
    public String name;
    public String email;
    public int credit;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
