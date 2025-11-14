package domain;

public class NeznamyZakaznik extends Zakaznik {
    private static final NeznamyZakaznik INSTANCE = new NeznamyZakaznik();

    private NeznamyZakaznik() {
        super(-1, "Neznámý zákazník", "unknown@example.com", 0);
    }

    public static NeznamyZakaznik instance() { return INSTANCE; }

    @Override
    protected void validate() {
        super.validate();
        // Unknown customer is always valid special case
    }
}


//Special case