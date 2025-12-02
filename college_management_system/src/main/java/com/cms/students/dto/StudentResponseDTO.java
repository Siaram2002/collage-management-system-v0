package com.cms.students.dto;

import com.cms.students.enums.StudentStatus;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {

	private String firstName;
	private String middleName;
	private String lastName;

	private LocalDate dob;

	private String gender;

	private String departmentName;

	private String courseName;

	private Integer admissionYear;
	private String admissionNumber;

	private String rollNumber;

	private StudentStatus status;

	private String bloodGroup;

	private String photoUrl;
	private String qrUrl;

}
