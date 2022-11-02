package com.swp.hr_backend.utils;

import java.util.Arrays;

public enum AccountRole {
	CANDIDATE(5),
	HRMANAGER(3),
	HREMPLOYEE(2),
	ADMIN(4),
	EMPLOYEE(1),;
	
	private int value;

	AccountRole(int value) {
		this.value = value;
	}
    
    public static AccountRole valueOf(int value) {
		return Arrays.stream(values()).filter(rs -> rs.value == value).findFirst().get();
	}
    
    public static int numOfRole(String role) {
    	if(role.equalsIgnoreCase(EMPLOYEE.toString())) {
    		return EMPLOYEE.value;
    	} else if(role.equalsIgnoreCase(HREMPLOYEE.toString())) {
    		return HREMPLOYEE.value;
    	} else if(role.equalsIgnoreCase(HRMANAGER.toString())) {
    		return HRMANAGER.value;
    	} else if(role.equalsIgnoreCase(ADMIN.toString())) {
    		return ADMIN.value;
    	} else if(role.equalsIgnoreCase(CANDIDATE.toString())) {
    		return CANDIDATE.value;
    	}
    	return -1;
    }
}
