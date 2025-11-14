package domain;

public class Trener {
    public int id;
    public String name;
    public String email;
    public String password;
    public String speciality;
    public String clas;

    public Trener(int id, String name, String email, String password, String speciality, String clas) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.speciality = speciality;
        this.clas = clas;
    }

    public void updateClas(String newClas) {
        this.clas = newClas;
        System.out.println(name + " má nový rozvrh: " + newClas);
    }

    public void infoPrint() {
        System.out.println("Trenér: " + name + " | Specializace: " + speciality + " | Rozvrh: " + clas);
    }
}