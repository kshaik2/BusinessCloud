package com.infor.cloudsuite.dto;

import org.hibernate.validator.constraints.Length;

import com.infor.cloudsuite.service.StringDefs;
import com.infor.cloudsuite.validation.PasswordsMatch;

/**
 * User: bcrow
 * Date: 10/27/11 10:27 AM
 */
@PasswordsMatch
public interface PasswordCompleter {

    @Length(min = StringDefs.PASSWORD_MIN_LENGTH, message = "{com.infor.cloudsuite.passwordTooShort}")
    String getPassword();

    String getPassword2();
}
