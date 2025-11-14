package interfaces;

public interface IPaymentGateway {

    boolean charge(int customerId, int amountCzk, String reason);
}


