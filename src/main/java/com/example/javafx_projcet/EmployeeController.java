package com.example.javafx_projcet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;


import java.math.BigDecimal;
import java.sql.*;

import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    Connection con =null;
    PreparedStatement st=null;
    ResultSet rs =null;

    @FXML
    private TextField age;

    @FXML
    private Button btnadd;

    @FXML
    private Button btndelete;

    @FXML
    private Button btnupdate;
    @FXML
    private Button statistic;

    @FXML
    private TextField department;

    @FXML
    private TextField id;

    @FXML
    private TextField name;

    @FXML
    private TextField salary;
@FXML
    private Label employeesPerDepartment;
    @FXML
    private Label mostEmployeDept;

    @FXML
    private Label mostEmployeesCount;

    @FXML
    private Label totalEmployeesLabel;

    @FXML
    private Label totalSalaryMass;

    @FXML
    private TableColumn<Employee, Integer> colage;

    @FXML
    private TableColumn<Employee, String> coldepartment;

    @FXML
    private TableColumn<Employee, String> colfullname;

    @FXML
    private TableColumn<Employee, Integer> colid;

    @FXML
    private TableColumn<Employee, BigDecimal> colsalary;
    @FXML
    private TableView <Employee> table;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showEmpolyees();
    }

    public ObservableList<Employee> getEmployees(){
        ObservableList<Employee> employees = FXCollections.observableArrayList();
        String query;
        query = "select idemp,nomemp,salaire,age,nomdept from Department, Employee where RefDept=IdDept;";
        con= DaoFactory.connnect_to_db();

        try {
            st =con.prepareStatement(query);
            rs=st.executeQuery();
            while (rs.next()){
                Employee employee = new Employee();
                employee.setId(rs.getInt("idemp"));
                employee.setName(rs.getString("nomemp"));
                employee.setAge(rs.getInt("age"));
                employee.setSalary(rs.getBigDecimal("salaire"));
                employee.setDepartment(rs.getString("nomdept"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return employees;
    };

    public void showEmpolyees(){
        ObservableList<Employee> list = getEmployees();
        table.setItems(list);
        colid.setCellValueFactory(new PropertyValueFactory<Employee,Integer>("id"));
        colfullname.setCellValueFactory(new PropertyValueFactory<Employee,String>("name"));
        colsalary.setCellValueFactory(new PropertyValueFactory<Employee,BigDecimal>("salary"));
        colage.setCellValueFactory(new PropertyValueFactory<Employee,Integer>("age"));
        coldepartment.setCellValueFactory(new PropertyValueFactory<Employee,String>("department"));


    }

    @FXML
    void addEmployee(ActionEvent event) {
        String insertEmployee = "INSERT INTO Employee (NomEmp, Salaire, Age, RefDept) VALUES (?, ?, ?, ?)";
        String insertDepartment = "INSERT INTO Department (NomDept) VALUES (?)";
        String selectDepartment = "SELECT IdDept FROM Department WHERE NomDept = ?";
        String departmentName = department.getText();

        try (Connection con = DaoFactory.connnect_to_db();
             PreparedStatement insertEmployeeStmt = con.prepareStatement(insertEmployee);
             PreparedStatement insertDepartmentStmt = con.prepareStatement(insertDepartment);
             PreparedStatement selectDepartmentStmt = con.prepareStatement(selectDepartment)) {


            insertEmployeeStmt.setString(1, name.getText());
            insertEmployeeStmt.setBigDecimal(2, new BigDecimal(salary.getText()));
            insertEmployeeStmt.setInt(3, Integer.parseInt(age.getText()));


            selectDepartmentStmt.setString(1, departmentName);
            ResultSet resultSet = selectDepartmentStmt.executeQuery();

            int departmentId;

            if (resultSet.next()) {

                departmentId = resultSet.getInt("IdDept");
            } else {
                // unfrontely the department isn't exict so we create a new one;
                insertDepartmentStmt.setString(1, departmentName);
                insertDepartmentStmt.executeUpdate();


                ResultSet generatedKeys = insertDepartmentStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    departmentId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve the ID of the newly created department.");
                }
            }


            insertEmployeeStmt.setInt(4, departmentId);


            insertEmployeeStmt.executeUpdate();


            showEmpolyees();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void getData(MouseEvent event) {
        Employee employe = table.getSelectionModel().getSelectedItem();
        id.setText(String.valueOf(employe.getId()));
        name.setText(employe.getName());
        age.setText(String.valueOf(employe.getAge()));
        department.setText(employe.getDepartment());
        salary.setText(String.valueOf(employe.getSalary()));
        btnadd.setDisable(true);
    }

    @FXML
    void deleteEmployee(ActionEvent event) {
        String deleteEmployee = "DELETE FROM Employee WHERE IdEmp = ?";

        try (Connection con = DaoFactory.connnect_to_db();
             PreparedStatement deleteEmployeeStmt = con.prepareStatement(deleteEmployee)) {

            int employeeId = Integer.parseInt(id.getText());

            deleteEmployeeStmt.setInt(1, employeeId);

            int rowsAffected = deleteEmployeeStmt.executeUpdate();

            if (rowsAffected > 0) {
                showEmpolyees();

            } else {

                System.out.println("The selected employee ID was not found.");
            }
            btnadd.setDisable(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML

    void modifyEmployee(ActionEvent event) {
        String updateEmployee = "UPDATE Employee SET NomEmp = ?, Salaire = ?, Age = ?, RefDept = ? WHERE IdEmp = ?";
        String selectDepartment = "SELECT IdDept FROM Department WHERE NomDept = ?";
        String departmentName = department.getText();

        try (Connection con = DaoFactory.connnect_to_db();
             PreparedStatement updateEmployeeStmt = con.prepareStatement(updateEmployee);
             PreparedStatement selectDepartmentStmt = con.prepareStatement(selectDepartment)) {

            // Set employee parameters
            updateEmployeeStmt.setString(1, name.getText());
            updateEmployeeStmt.setBigDecimal(2, new BigDecimal(salary.getText()));
            updateEmployeeStmt.setInt(3, Integer.parseInt(age.getText()));
            updateEmployeeStmt.setInt(5, Integer.parseInt(id.getText()));

            // Check if the department already exists
            selectDepartmentStmt.setString(1, departmentName);
            ResultSet resultSet = selectDepartmentStmt.executeQuery();

            int departmentId;

            if (resultSet.next()) {
                // Existing department, use its ID
                departmentId = resultSet.getInt("IdDept");
            } else {
                // Handle the case where the department doesn't exist (optional)
                throw new SQLException("The specified department does not exist.");
            }

            // Set the department ID for the employee
            updateEmployeeStmt.setInt(4, departmentId);

            // Execute the employee update
            int rowsAffected = updateEmployeeStmt.executeUpdate();

            if (rowsAffected > 0) {
                // Employee updated successfully
                showEmpolyees();
            } else {
                // Handle the case where the employee ID was not found
                System.out.println("The selected employee ID was not found.");
            }
            btnadd.setDisable(false);


        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately

        }
    }


    @FXML
    void displayStat(ActionEvent event) {
        String totalEmployeesQuery = "SELECT COUNT(*) FROM Employee";
        String departmentWithMostEmployeesQuery = "SELECT nomdept, COUNT(*) AS employeeCount FROM Department " +
                "JOIN Employee ON RefDept = IdDept GROUP BY nomdept ORDER BY employeeCount DESC LIMIT 1";
        String employeesPerDepartmentQuery = "SELECT nomdept, COUNT(*) AS employeeCount FROM Department " +
                "LEFT JOIN Employee ON RefDept = IdDept GROUP BY nomdept";
        String totalSalaryMassQuery = "SELECT SUM(Salaire) FROM Employee";

        try (Connection con = DaoFactory.connnect_to_db();
             Statement statement = con.createStatement()) {

            // Get total number of employees
            ResultSet totalEmployeesResult = statement.executeQuery(totalEmployeesQuery);
            if (totalEmployeesResult.next()) {
                int totalEmployees = totalEmployeesResult.getInt(1);
                totalEmployeesLabel.setText("Total number of employees: " + totalEmployees);
            }

            // Get department with the most employees
            ResultSet mostEmployeesResult = statement.executeQuery(departmentWithMostEmployeesQuery);
            if (mostEmployeesResult.next()) {
                String mostEmployeesDepartment = mostEmployeesResult.getString("nomdept");
                int mostEmployeesCountValue = mostEmployeesResult.getInt("employeeCount");
                mostEmployeDept.setText("Department with the most employees: " + mostEmployeesDepartment);
                mostEmployeesCount.setText("Employees: " + mostEmployeesCountValue);
            }

            // Get number of employees for each department
            ResultSet employeesPerDepartmentResult = statement.executeQuery(employeesPerDepartmentQuery);
            StringBuilder employeesPerDepartmentText = new StringBuilder("Employees per department:\n");
            while (employeesPerDepartmentResult.next()) {
                String departmentName = employeesPerDepartmentResult.getString("nomdept");
                int employeeCount = employeesPerDepartmentResult.getInt("employeeCount");
                employeesPerDepartmentText.append(departmentName).append(": ").append(employeeCount).append("\n");
            }
            employeesPerDepartment.setText(employeesPerDepartmentText.toString());

            // Get total salary mass of employees
            ResultSet totalSalaryMassResult = statement.executeQuery(totalSalaryMassQuery);
            if (totalSalaryMassResult.next()) {
                BigDecimal totalSalaryMassValue = totalSalaryMassResult.getBigDecimal(1);
                totalSalaryMass.setText("Total salary mass of employees: " + totalSalaryMassValue);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately

        }
    }






}
