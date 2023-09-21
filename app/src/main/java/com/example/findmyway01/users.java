package com.example.findmyway01;

public class users {

        private String Fname;
        private String Lname;
        private String Email;
        private String Pass;
        private String Cpass;
        private String cont;
        private String Year;
        private String Month;
        private String Day;
        private String Dist;
        private String City;

    public users(){

    }
    public users(String Fname,String Lname, String Email,String Pass,String Cpass,String cont,String Year,String Month,String Day,String Dist,String City) {
        this.Fname = Fname;
        this.Lname = Lname;
        this.Email = Email;
        this.Pass = Pass;
        this.Cpass = Cpass;
        this. cont = cont;
        this.Year = Year;
        this.Month = Month;
        this.Day = Day;
        this.Dist = Dist;
        this.City = City;
    }
    public String getFname() {
        return Fname;
    }

    public void setFname(String Fname) {
        this.Fname = Fname;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String Lname) {
        this.Lname = Lname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPass() {
        return Pass;
    }

    public void setPass(String Pass) {
        this.Pass = Pass;
    }

    public String getCpass() {
        return Cpass;
    }

    public void setCpass(String Cpass) {
        this.Cpass = Cpass;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String Year) {
        this.Year = Year;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String Month) {
        this.Month = Month;
    }

    public String getCont() {
        return cont;
    }

    public void setCont(String cont) {
        this.cont = cont;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String Day) {
        this.Day = Day;
    }

    public String getDistrict() {
        return Dist;
    }

    public void setDistrict(String Dist) {
        this.Dist = Dist;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String City) {
        this.City = City;
    }

}
