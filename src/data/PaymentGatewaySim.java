package data;

import interfaces.IPaymentGateway;

import java.util.Random;

public class PaymentGatewaySim implements IPaymentGateway {
    private final Random random = new Random();

    @Override
    public boolean charge(int customerId, int amountCzk, String reason) {
        //approve amounts <= 500 CZK; otherwise 80% chance approval
        boolean approved = amountCzk <= 500 || random.nextInt(10) < 8;
        System.out.println("[PAYMENT SIM] " + (approved ? "APPROVED" : "DECLINED") +
                " charge for customer " + customerId + ": " + amountCzk + " CZK (" + reason + ")");
        return approved;
    }
}


