package com.example.employee.DTO;

public class EmployeeDTO {
    private Long id;
    private String empName;
    private String role;
    private String image; // Base64 image from DB
    private String qr;    // Base64 QR

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getEmpName() {
        return empName;
    }
    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getQr() {
        return qr;
    }
    public void setQr(String qr) {
        this.qr = qr;
    }
}
