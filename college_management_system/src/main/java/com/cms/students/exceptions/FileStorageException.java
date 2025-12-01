package com.cms.students.exceptions;

public class FileStorageException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileStorageException(String message, Throwable cause) { super(message, cause); }

	public FileStorageException(String msg) {
		// TODO Auto-generated constructor stub
		super(msg);
	}
}