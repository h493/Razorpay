package com.capstone.razorpay.common.entity;


import jakarta.persistence.Embeddable;


@Embeddable
public class Money {

    private int amountUnits;
    private String currency;

    protected Money() {
        // required by JPA
    }

    private Money(int amountUnits, String currency) {
        this.amountUnits = amountUnits;
        this.currency = currency;
    }

    public static Money inr(int amountUnits){
        return new Money(amountUnits, "INR");
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currencies do not match");
        }
        return new Money(this.amountUnits + other.amountUnits, this.currency);
    }

    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currencies do not match");
        }
        return new Money(this.amountUnits - other.amountUnits, this.currency);
    }
}
