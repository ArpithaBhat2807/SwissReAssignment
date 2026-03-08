package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeAnalyzer {
    private static final double MIN_MANAGER_RATIO = 1.2;
    private static final double MAX_MANAGER_RATIO = 1.5;
    private static final int MAX_REPORTING_DEPTH = 4;

    public static void main(String[] args) throws Exception {

        /*
         * NOTE:
         * TC001: Involves all test cases where hierarchy is violated, few managers earn more salary and few managers earn less salary
         * TC002: Normal test case: Where no constraints are violated: No output
         * TC003: When manager earns less salary than subordinates
         * TC004: When manager earns more salary than subordinates
         * TC005: When deep hierarchy constraint is validated
         *
         * I have commented out the rest of the test cases and kindly run the test cases individually. JUNIT has been
         * written for the first test file which includes all test cases*/
        String filePath = "employees_TC001.csv";
//        String filePath = "employees_TC002.csv";
//        String filePath = "employees_TC003.csv";
//        String filePath = "employees_TC004.csv";
//        String filePath = "employees_TC005.csv";
//        String filePath = "wrong_file.csv";

        Map<Integer, Employee> employees = new HashMap<>();
        Map<Integer, List<Employee>> reportingStructure = new HashMap<>();

        loadEmployees(filePath, employees, reportingStructure);

        validateManagerSalaries(employees, reportingStructure);
        validateReportingLines(employees);

    }

    //This method loads the csv file
    private static void loadEmployees(String filePath,
                                      Map<Integer, Employee> employees,
                                      Map<Integer, List<Employee>> reportingStructure) throws IOException {

        InputStream inputStream = EmployeeAnalyzer.class
                .getClassLoader()
                .getResourceAsStream(filePath);

        if (inputStream == null) {
            throw new FileNotFoundException("File not found in resources: " + filePath);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",");

                int id = Integer.parseInt(parts[0]);
                String first = parts[1];
                String last = parts[2];
                double salary = Double.parseDouble(parts[3]);

                Integer managerId = null;
                if (parts.length > 4 && !parts[4].isEmpty()) {
                    managerId = Integer.parseInt(parts[4]);
                }

                Employee emp = new Employee(id, first, last, salary, managerId);
                employees.put(id, emp);

                if (managerId != null) {
                    reportingStructure.computeIfAbsent(managerId, k -> new ArrayList<>()).add(emp);
                }
            }
        }
    }

    //This method is used to check if managers salary is in expected range
    private static void validateManagerSalaries(Map<Integer, Employee> employees,
                                             Map<Integer, List<Employee>> reportingStructure) {

        for (Integer managerId : reportingStructure.keySet()) {

            Employee manager = employees.get(managerId);
            List<Employee> subs = reportingStructure.get(managerId);

            double total = 0;
            for (Employee e : subs) {
                total += e.salary;
            }

            double avgSalary = total / subs.size();

            double minAllowed = avgSalary * MIN_MANAGER_RATIO;
            double maxAllowed = avgSalary * MAX_MANAGER_RATIO;

            if (manager.salary < minAllowed) {
                System.out.println("Manager " + manager.firstName +
                        " earns LESS than required by " +
                        (minAllowed - manager.salary));
            }

            if (manager.salary > maxAllowed) {
                System.out.println("Manager " + manager.firstName +
                        " earns MORE than allowed by " +
                        (manager.salary - maxAllowed));
            }
        }
    }

    //This method checks the reporting line hierarchy
    private static void validateReportingLines(Map<Integer, Employee> employees) {

        Map<Integer, Integer> depthCache = new HashMap<>();

        for (Employee emp : employees.values()) {

            int depth = 0;
            Integer managerId = emp.managerId;

            while (managerId != null) {

                if (depthCache.containsKey(managerId)) {
                    depth += depthCache.get(managerId) + 1;
                    break;
                }

                depth++;
                managerId = employees.get(managerId).managerId;
            }

            depthCache.put(emp.id, depth);

            if (depth > MAX_REPORTING_DEPTH) {
                System.out.println(emp.firstName +
                        " has reporting line too long by " +
                        (depth - MAX_REPORTING_DEPTH));
            }
        }
    }
}
