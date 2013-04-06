package com.inbuy.ucommunity.util;

public class ErrorCode {
	public final static int INTERNAL_ERROR = 1;
	public final static int NO_NETWORK = 2;

	public static int errorId(int ecode) {
		int id = 0;
		switch (ecode) {
		case INTERNAL_ERROR:
			break;
		case NO_NETWORK:
			break;
		default:
			id = 0;
			break;
		}

		return id;
	}
}
