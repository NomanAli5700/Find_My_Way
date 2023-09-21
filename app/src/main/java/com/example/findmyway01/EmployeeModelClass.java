package com.example.findmyway01;

public class EmployeeModelClass {


    private String Fname_1;
    private String Lname_1;
    private String Email;
    private String Des_1;

    public EmployeeModelClass( String Fname_1,String Lname_1, String Email,String Des_1) {

        this.Fname_1 = Fname_1;
        this.Lname_1 = Lname_1;
        this.Email = Email;
        this.Des_1=Des_1;

    }

    public EmployeeModelClass() {
        // This constructor is required by Firebase for deserialization
        // It doesn't need to do anything, or you can initialize default values here
    }

    public String getFname_1() {
        return Fname_1;
    }
    public void setFname_1(String Fname_1) {
        this.Fname_1 = Fname_1;
    }

    public String getLname_1() {
        return Lname_1;
    }

    public void setLname(String Lname_1) {
        this.Lname_1 = Lname_1;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }
    public String getDes_1() {
        return Des_1;
    }

    public void setDes_1(String Des_1) {
        this.Des_1 = Des_1;
    }

}
