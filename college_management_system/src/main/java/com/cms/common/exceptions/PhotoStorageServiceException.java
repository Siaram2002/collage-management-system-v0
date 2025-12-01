package com.cms.common.exceptions;

import java.io.IOException;

public class PhotoStorageServiceException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PhotoStorageServiceException(String msg) {
		super(msg);
	}

	public PhotoStorageServiceException(String msg, IOException e) {
		// TODO Auto-generated constructor stub
		super(msg,e);
	}

}
