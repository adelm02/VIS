package domain;

public abstract class Zakaznik extends BaseEntity {
    public int id;
    public String name;
    public String email;
    public int credit;

    public Zakaznik(int id, String name, String email, int credit) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.credit = credit;
    }

    @Override
    protected void validate() {
        super.validate();
        if (name == null || name.isEmpty()) addValidationError("Jméno zákazníka je povinné");
        if (email == null || !email.contains("@")) addValidationError("Email zákazníka je neplatný");
        if (credit < 0) addValidationError("Kredit nesmí být záporný");
    }
}