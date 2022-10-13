package com.swp.hr_backend.utils;

import java.util.Arrays;
import java.util.Optional;

public enum ResultStatus {
	NONE(0), PENDING(1), FIRSTROUND(2), SECONDROUND(3), NOTPASS(4), PASS(5);

	private int value;

	ResultStatus(int value) {
		this.value = value;
	}

	public static Optional<ResultStatus> valueOf(int value) {
		return Arrays.stream(values()).filter(rs -> rs.value == value).findFirst();
	}
	
	public static int numOfResultStatus(ResultStatus status) {
		switch (status) {
		case NONE:
			return 0;
		case PENDING:
			return 1;
		case FIRSTROUND:
			return 2;
		case SECONDROUND:
			return 3;
		case NOTPASS:
			return 4;
		case PASS:
			return 5;
		}
		return -1;
	}
}
