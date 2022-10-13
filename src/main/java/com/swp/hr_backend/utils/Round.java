package com.swp.hr_backend.utils;

import java.util.Arrays;
import java.util.Optional;

public enum Round {
    PENDING(0),
    ROUND1(1),
    ROUND2(2),
    PASS(3),
    NOT_PASS(4),;
	
	private int value;

	Round(int value) {
		this.value = value;
	}
    
    public static Round valueOf(int value) {
		return Arrays.stream(values()).filter(rs -> rs.value == value).findFirst().get();
	}
}
