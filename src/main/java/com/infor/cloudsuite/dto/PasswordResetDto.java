package com.infor.cloudsuite.dto;

/**
 * User: bcrow
 * Date: 10/20/11 3:29 PM
 */
public class PasswordResetDto implements PasswordCompleter{
    private String email;
    private String password;
    private String password2;
    private boolean create=false;
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}


    
}
