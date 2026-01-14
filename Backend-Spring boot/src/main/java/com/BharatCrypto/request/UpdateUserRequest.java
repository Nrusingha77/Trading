package com.BharatCrypto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Pattern(regexp = "^[a-zA-Z\\s.]*$", message = "Full name should only contain letters, spaces, and dots")
    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
    private String fullName;

    // Allows an empty string OR a valid Indian mobile number
    @Pattern(regexp = "^$|^(\\+91|0)?[6-9][0-9]{9}$", message = "Invalid Indian mobile number.")
    private String mobile;

    @Size(max = 50, message = "Date of birth cannot exceed 50 characters")
    private String dateOfBirth;

    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Nationality should only contain letters")
    @Size(max = 50, message = "Nationality cannot exceed 50 characters")
    private String nationality;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "City should only contain letters")
    @Size(max = 50, message = "City cannot exceed 50 characters")
    private String city;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid Indian PIN code (must be 6 digits)")
    private String postcode;

    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Country should only contain letters")
    @Size(max = 50, message = "Country cannot exceed 50 characters")
    private String country;
}