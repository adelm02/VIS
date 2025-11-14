package data;

import interfaces.IPaymentGateway;

public class PaymentGatewayStub implements IPaymentGateway {
    @Override
    public boolean charge(int customerId, int amountCzk, String reason) {
        System.out.println("[PAYMENT STUB] Approved charge for customer " + customerId + ": " + amountCzk + " CZK (" + reason + ")");
        return true;
    }
}


