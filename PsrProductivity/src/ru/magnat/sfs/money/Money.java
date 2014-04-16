package ru.magnat.sfs.money;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

public class Money implements Comparable<Money> {	

	private final BigDecimal mValue;
	private final Integer mRoundMode = BigDecimal.ROUND_HALF_UP;
	private final BigDecimal mHundred = new BigDecimal(100);
	
	private Money() {
		mValue = BigDecimal.ZERO.setScale(2, mRoundMode);
	}
	
	private Money(BigDecimal value) {
		mValue = value.setScale(2, mRoundMode);
	}
	
	public Money add(Money value) {
		return new Money(mValue.add(value.mValue));
	} 
	
	public Money subtract(Money value) {
		return new Money(mValue.subtract(value.mValue));
	}
	
	public Money multiply(Integer value) {
		return new Money(mValue.multiply(new BigDecimal(value)));
	} 
	
	public Money discount(Integer value) {
		if (value == 0) {
			return Money.valueOf(this); 
		}
		return new Money(mValue.subtract(mValue.multiply(new BigDecimal(value).divide(mHundred))));
	}
	
	public Money discount(Money value) {
		if (value.mValue.compareTo(BigDecimal.ZERO) == 0) {
			return Money.valueOf(this); 
		}
		return new Money(mValue.subtract(mValue.multiply(new BigDecimal(value.mValue.toString()).divide(mHundred))));
	}
	
	public Money margin(Integer value) {
		if (value == 0) {
			return Money.valueOf(this); 
		}
		return new Money(mValue.add(mValue.multiply(new BigDecimal(value).divide(mHundred))));
	}
	
	public Money interest(Integer value) {
		return new Money(mValue.multiply(new BigDecimal(value).divide(mHundred)));  
	}
	
	public Money increaseOf(Integer value) {
		return new Money(mValue.add(mValue.multiply(new BigDecimal(value).divide(mHundred))));
	} 
	
	public Money difference(Money value) {
		return new Money(mValue.divide(value.mValue, 3, BigDecimal.ROUND_CEILING).multiply(mHundred).subtract(mHundred));
	}
	
	public static Money valueOf(Short value) {
		return new Money(new BigDecimal(value));
	}
	
	public static Money valueOf(Integer value) {
		return new Money(new BigDecimal(value));
	}
	
	public static Money valueOf(Float value) {
		return new Money(new BigDecimal(value));
	}
	
	public static Money valueOf(Double value) {
		return new Money(new BigDecimal(value));
	}
	
	public static Money valueOf(String value) {
		return new Money(new BigDecimal(value));
	}
	
	public static Money valueOf(Money value) {
		return new Money(value.mValue);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) 
			return true;
		if (!(o instanceof Money))
			return false;
		if (this.mValue.compareTo(((Money) o).mValue) == 0) {
			return true;
		}
		return false; 
	}
	
	@Override
	public String toString() {
		return mValue.toString();
	}
	
	public String toSymbolString() {
		return toString() + " " + Currency.getInstance(Locale.getDefault()).getSymbol();
	}
	
	public String toSymbolString(Locale locale) {
		return toString() + " " + Currency.getInstance(locale).getSymbol(); 
	}
	
	public String toSymbolString(Currency currency) {
		return toString() + " " + currency.getSymbol(); 
	}
	
	@Override
	public int hashCode() {
		return mValue.hashCode();
	}
	
	@Override
	public int compareTo(Money o) {	
		return mValue.compareTo(o.mValue);
	}
	
} 
