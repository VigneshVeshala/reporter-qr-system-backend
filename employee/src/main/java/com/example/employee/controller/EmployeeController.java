package com.example.employee.controller;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.employee.DTO.EmployeeDTO;
import com.example.employee.entity.Employee;
import com.example.employee.repository.EmployeeRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/employees")
public class EmployeeController {

	@Autowired
	private EmployeeRepository repo;

	// QR generator
	private String generateQR(String text) {
		try {
			QRCodeWriter qrWriter = new QRCodeWriter();
			var matrix = qrWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(MatrixToImageWriter.toBufferedImage(matrix), "png", out);

			return Base64.getEncoder().encodeToString(out.toByteArray());
		} catch (Exception e) {
			return null;
		}
	}

	// FETCH ALL
	@GetMapping
	public List<EmployeeDTO> getEmployees() {
		return repo.findAll().stream().map(emp -> {
			EmployeeDTO dto = new EmployeeDTO();
			dto.setId(emp.getId());
			dto.setEmpName(emp.getEmpName());
			dto.setRole(emp.getRole());
			dto.setImage(emp.getImage() == null ? null : Base64.getEncoder().encodeToString(emp.getImage()));
			dto.setQr(generateQR(emp.getId() + " | " + emp.getEmpName() + " | " + emp.getRole()));
			return dto;
		}).toList();
	}

	// FETCH BY ID (SCANNER USES THIS)
	@GetMapping("/{id}")
	public EmployeeDTO getEmployeeById(@PathVariable Long id) {
		return repo.findById(id).map(emp -> {
			EmployeeDTO dto = new EmployeeDTO();
			dto.setId(emp.getId());
			dto.setEmpName(emp.getEmpName());
			dto.setRole(emp.getRole());
			dto.setImage(emp.getImage() == null ? null : Base64.getEncoder().encodeToString(emp.getImage()));
			dto.setQr(generateQR(emp.getId() + " | " + emp.getEmpName() + " | " + emp.getRole()));
			return dto;
		}).orElse(null);
	}

	// SEARCH (Frontend filter)
	@GetMapping("/search")
	public List<EmployeeDTO> search(@RequestParam String name, @RequestParam String role) {
		return repo.findAll().stream().filter(emp -> emp.getEmpName().toLowerCase().contains(name.toLowerCase())
				&& emp.getRole().toLowerCase().contains(role.toLowerCase())).map(emp -> {
					EmployeeDTO dto = new EmployeeDTO();
					dto.setId(emp.getId());
					dto.setEmpName(emp.getEmpName());
					dto.setRole(emp.getRole());
					dto.setImage(emp.getImage() == null ? null : Base64.getEncoder().encodeToString(emp.getImage()));
					dto.setQr(generateQR(emp.getId() + " | " + emp.getEmpName() + " | " + emp.getRole()));
					return dto;
				}).toList();
	}

	// ADD EMPLOYEE (with image)
	@PostMapping("/add")
	public void add(@RequestParam String empName, @RequestParam String role,
			@RequestParam(required = false) MultipartFile image) throws Exception {

		Employee emp = new Employee();
		emp.setEmpName(empName);
		emp.setRole(role);

		if (image != null) {
			emp.setImage(image.getBytes());
		}

		repo.save(emp);
	}

	// DELETE
	@DeleteMapping("/delete/{id}")
	public void delete(@PathVariable Long id) {
		repo.deleteById(id);
	}

	// UPDATE
	@PutMapping("/update/{id}")
	public void update(@PathVariable Long id, @RequestParam String empName, @RequestParam String role,
			@RequestParam(required = false) MultipartFile image) throws Exception {

		repo.findById(id).ifPresent(emp -> {
			emp.setEmpName(empName);
			emp.setRole(role);

			try {
				if (image != null)
					emp.setImage(image.getBytes());
			} catch (Exception ignored) {
			}

			repo.save(emp);
		});
	}
}
