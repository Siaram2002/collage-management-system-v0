package com.cms.students.exceptions;

public class DuplicateStudentException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateStudentException(String message) { super(message); }
}