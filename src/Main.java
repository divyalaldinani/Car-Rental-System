import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static java.lang.System.out;

class Vehicle {
    private String vehicleId;
    private String brand;
    private String model;
    private double pricePerDay;
    private boolean available;

    public Vehicle(String vehicleId, String brand, String model, double pricePerDay) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.pricePerDay = pricePerDay;
        this.available = true;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public double calculateTotalCost(int rentalDays) {
        return pricePerDay * rentalDays;
    }

    public boolean isAvailable() {
        return available;
    }

    public void rentOut() {
        available = false;
    }

    public void returnVehicle() {
        available = true;
    }
}

class Customer {
    private String customerId;
    private String name;
    private String phoneNumber;

    public Customer(String customerId, String name, String phoneNumber) {
        this.customerId = customerId;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}

class Rental {
    private Vehicle vehicle;
    private Customer customer;
    private int days;

    public Rental(Vehicle vehicle, Customer customer, int days) {
        this.vehicle = vehicle;
        this.customer = customer;
        this.days = days;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getDays() {
        return days;
    }
}

class VehicleRentalService {
    private List<Vehicle> vehicles;
    private List<Customer> customers;
    private List<Rental> rentals;

    public VehicleRentalService() {
        vehicles = new ArrayList<>();
        customers = new ArrayList<>();
        rentals = new ArrayList<>();
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, int days) {
        if (vehicle.isAvailable()) {
            vehicle.rentOut();
            rentals.add(new Rental(vehicle, customer, days));
        } else {
            out.println("Sorry, the vehicle is currently unavailable.");
            out.flush();
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer returningCustomer) {
        if (!vehicle.isAvailable()) {
            Rental rentalToRemove = null;

            for (Rental rental : rentals) {
                Customer originalCustomer = rental.getCustomer();
                if (rental.getVehicle().equals(vehicle)) {
                    if (originalCustomer.getName().equals(returningCustomer.getName()) &&
                            originalCustomer.getPhoneNumber().equals(returningCustomer.getPhoneNumber())) {
                        rentalToRemove = rental;
                    } else {
                        out.println("Error: The customer details do not match the rental record.");
                        out.flush();
                        return;
                    }
                }
            }

            if (rentalToRemove != null) {
                rentals.remove(rentalToRemove);
                vehicle.returnVehicle();
                out.println("Vehicle returned successfully.");
                out.flush();
            } else {
                out.println("This vehicle was not rented out.");
                out.flush();
            }
        } else {
            out.println("This vehicle is not currently rented out.");
            out.flush();
        }
    }


    public void showMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            out.println("---- Vehicle Rental Service ----");
            out.println("1. Rent a Vehicle");
            out.println("2. Return a Vehicle");
            out.println("3. Search Available Vehicles by Brand");
            out.println("4. View All Rentals");
            out.println("5. Exit");
            out.print("Choose an option: ");
            out.flush();

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> rentVehicleOption(scanner);
                case 2 -> returnVehicleOption(scanner);
                case 3 -> searchByBrandOption(scanner);
                case 4 -> viewAllRentalsOption();
                case 5 -> {
                    out.println("Thank you for using our Rental Service!");
                    out.flush();
                    return;
                }
                default -> out.println("Invalid option. Please try again.");
            }
        }
    }

    private void rentVehicleOption(Scanner scanner) {
        out.println("\n-- Rent a Vehicle --\n");
        out.print("Enter your name: ");
        String customerName = scanner.nextLine();

        out.print("Enter your phone number: ");
        String customerPhoneNumber = scanner.nextLine();

        out.println("\nAvailable Vehicles:");
        out.flush();
        boolean hasAvailableVehicles = false;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.isAvailable()) {
                out.println(vehicle.getVehicleId() + " - " + vehicle.getBrand() + " " + vehicle.getModel());
                hasAvailableVehicles = true;
            }
        }

        if (!hasAvailableVehicles) {
            out.println("No vehicles available for rent.");
            out.flush();
            return;
        }

        out.print("\nEnter the vehicle ID you want to rent: ");
        out.flush();
        String vehicleId = scanner.nextLine();

        Customer newCustomer = new Customer("CUS" + (customers.size() + 1), customerName, customerPhoneNumber);
        addCustomer(newCustomer);

        Vehicle selectedVehicle = null;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleId().equals(vehicleId) && vehicle.isAvailable()) {
                selectedVehicle = vehicle;
                break;
            }
        }

        if (selectedVehicle != null) {
            out.print("Enter the number of days for rental: ");
            int rentalDays = scanner.nextInt();
            scanner.nextLine();
            double totalCost = selectedVehicle.calculateTotalCost(rentalDays);
            out.println("\n== Rental Information ==\n");
            out.println("Customer ID: " + newCustomer.getCustomerId());
            out.println("Customer Name: " + newCustomer.getName());
            out.println("Phone Number: " + newCustomer.getPhoneNumber());
            out.println("Vehicle: " + selectedVehicle.getBrand() + " " + selectedVehicle.getModel());
            out.println("Rental Days: " + rentalDays);
            out.printf("Total Cost: $%.2f%n", totalCost);

            out.print("\nConfirm rental (Y/N): ");
            out.flush();
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("Y")) {
                rentVehicle(selectedVehicle, newCustomer, rentalDays);
                out.println("\nVehicle rented successfully.");
            } else {
                out.println("\nRental canceled.");
            }
            out.flush();
        } else {
            out.println("\nInvalid vehicle ID or vehicle not available for rent.");
            out.flush();
        }
    }

    private void returnVehicleOption(Scanner scanner) {
        out.println("\n-- Return a Vehicle --\n");
        out.print("Enter your name: ");
        String customerName = scanner.nextLine();

        out.print("Enter your phone number: ");
        String customerPhoneNumber = scanner.nextLine();

        out.print("Enter the vehicle ID you want to return: ");
        out.flush();
        String vehicleId = scanner.nextLine();

        Vehicle vehicleToReturn = null;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleId().equals(vehicleId)) {
                vehicleToReturn = vehicle;
                break;
            }
        }

        if (vehicleToReturn != null) {
            if (!vehicleToReturn.isAvailable()) {
                Customer returningCustomer = new Customer(null, customerName, customerPhoneNumber);
                returnVehicle(vehicleToReturn, returningCustomer);
            } else {
                out.println("This vehicle is not currently rented out.");
                out.flush();
            }
        } else {
            out.println("Invalid vehicle ID.");
            out.flush();
        }
    }

    private void searchByBrandOption(Scanner scanner) {
        out.println("\n-- Search Vehicles by Brand --\n");
        out.print("Enter the brand name: ");
        out.flush();
        String brandName = scanner.nextLine();

        out.println("\nAvailable Vehicles of brand " + brandName + ":");
        out.flush();
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.isAvailable() && vehicle.getBrand().equalsIgnoreCase(brandName)) {
                out.println(vehicle.getVehicleId() + " - " + vehicle.getBrand() + " " + vehicle.getModel());
                out.flush();
                found = true;
            }
        }

        if (!found) {
            out.println("No available vehicles found for the brand " + brandName + ".");
            out.flush();
        }
    }

    private void viewAllRentalsOption() {
        out.println("\n-- View All Rentals --\n");
        out.flush();
        if (rentals.isEmpty()) {
            out.println("No rentals available.");
            out.flush();
        } else {
            for (Rental rental : rentals) {
                out.println("Customer: " + rental.getCustomer().getName() + " | Phone: " +
                        rental.getCustomer().getPhoneNumber() + " | Vehicle: " +
                        rental.getVehicle().getBrand() + " " + rental.getVehicle().getModel() + " | Days: " + rental.getDays());
                out.flush();
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        VehicleRentalService rentalService = new VehicleRentalService();

        Vehicle vehicle1 = new Vehicle("V001", "Toyota", "Corolla", 50.0);
        Vehicle vehicle2 = new Vehicle("V002", "Honda", "Civic", 65.0);
        Vehicle vehicle3 = new Vehicle("V003", "Kawasaki", "Ninja ZX-6R", 80.0);
        Vehicle vehicle4 = new Vehicle("V004", "Yamaha", "MT-07", 70.0);
        Vehicle vehicle5 = new Vehicle("V005", "Vespa", "Primavera", 40.0);
        Vehicle vehicle6 = new Vehicle("V006", "Honda", "Activa", 35.0);

        rentalService.addVehicle(vehicle1);
        rentalService.addVehicle(vehicle2);
        rentalService.addVehicle(vehicle3);
        rentalService.addVehicle(vehicle4);
        rentalService.addVehicle(vehicle5);
        rentalService.addVehicle(vehicle6);

        rentalService.showMenu();
    }
}
