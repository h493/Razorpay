package com.capstone.razorpay.common.entity;


import jakarta.persistence.Embeddable;
import lombok.*;


@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Money {

    private int amountUnits;
    private String currency;


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
