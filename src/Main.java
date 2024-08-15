import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Vehicle {
    private String vehicleId;
    private String make;
    private String model;
    private double pricePerDay;
    private boolean available;

    public Vehicle(String vehicleId, String make, String model, double pricePerDay) {
        this.vehicleId = vehicleId;
        this.make = make;
        this.model = model;
        this.pricePerDay = pricePerDay;
        this.available = true;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getMake() {
        return make;
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

class CarRentalService {
    private List<Vehicle> vehicles;
    private List<Customer> customers;
    private List<Rental> rentals;

    public CarRentalService() {
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
            System.out.println("Sorry, the vehicle is currently unavailable.");
        }
    }

    public void returnVehicle(Vehicle vehicle) {
        vehicle.returnVehicle();
        Rental rentalToRemove = null;
        for (Rental rental : rentals) {
            if (rental.getVehicle().equals(vehicle)) {
                rentalToRemove = rental;
                break;
            }
        }
        if (rentalToRemove != null) {
            rentals.remove(rentalToRemove);
        } else {
            System.out.println("This vehicle was not rented out.");
        }
    }

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("===== Car Rental Service =====");
            System.out.println("1. Rent a Vehicle");
            System.out.println("2. Return a Vehicle");
            System.out.println("3. Search Available Vehicles by Brand");
            System.out.println("4. View All Rentals");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1 -> rentVehicleOption(scanner);
                case 2 -> returnVehicleOption(scanner);
                case 3 -> searchByBrandOption(scanner);
                case 4 -> viewAllRentalsOption();
                case 5 -> {
                    System.out.println("Thank you for using the Car Rental Service!");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void rentVehicleOption(Scanner scanner) {
        System.out.println("\n-- Rent a Vehicle --\n");
        System.out.print("Enter your name: ");
        String customerName = scanner.nextLine();

        System.out.print("Enter your phone number: ");
        String customerPhoneNumber = scanner.nextLine();

        System.out.println("\nAvailable Vehicles:");
        for (Vehicle vehicle : vehicles) {
            if (vehicle.isAvailable()) {
                System.out.println(vehicle.getVehicleId() + " - " + vehicle.getMake() + " " + vehicle.getModel());
            }
        }

        System.out.print("\nEnter the vehicle ID you want to rent: ");
        String vehicleId = scanner.nextLine();

        System.out.print("Enter the number of days for rental: ");
        int rentalDays = scanner.nextInt();
        scanner.nextLine(); // Consume newline

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
            double totalCost = selectedVehicle.calculateTotalCost(rentalDays);
            System.out.println("\n== Rental Information ==\n");
            System.out.println("Customer ID: " + newCustomer.getCustomerId());
            System.out.println("Customer Name: " + newCustomer.getName());
            System.out.println("Phone Number: " + newCustomer.getPhoneNumber());
            System.out.println("Vehicle: " + selectedVehicle.getMake() + " " + selectedVehicle.getModel());
            System.out.println("Rental Days: " + rentalDays);
            System.out.printf("Total Cost: $%.2f%n", totalCost);

            System.out.print("\nConfirm rental (Y/N): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("Y")) {
                rentVehicle(selectedVehicle, newCustomer, rentalDays);
                System.out.println("\nVehicle rented successfully.");
            } else {
                System.out.println("\nRental canceled.");
            }
        } else {
            System.out.println("\nInvalid vehicle selection or vehicle not available for rent.");
        }
    }

    private void returnVehicleOption(Scanner scanner) {
        System.out.println("\n-- Return a Vehicle --\n");
        System.out.print("Enter the vehicle ID you want to return: ");
        String vehicleId = scanner.nextLine();

        Vehicle vehicleToReturn = null;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleId().equals(vehicleId) && !vehicle.isAvailable()) {
                vehicleToReturn = vehicle;
                break;
            }
        }

        if (vehicleToReturn != null) {
            Customer customer = null;
            for (Rental rental : rentals) {
                if (rental.getVehicle().equals(vehicleToReturn)) {
                    customer = rental.getCustomer();
                    break;
                }
            }

            if (customer != null) {
                returnVehicle(vehicleToReturn);
                System.out.println("Vehicle returned successfully by " + customer.getName());
            } else {
                System.out.println("Vehicle was not rented or rental information is missing.");
            }
        } else {
            System.out.println("Invalid vehicle ID or vehicle is not rented.");
        }
    }

    private void searchByBrandOption(Scanner scanner) {
        System.out.println("\n-- Search Vehicles by Brand --\n");
        System.out.print("Enter the brand name: ");
        String brandName = scanner.nextLine();

        System.out.println("\nAvailable Vehicles of brand " + brandName + ":");
        for (Vehicle vehicle : vehicles) {
            if (vehicle.isAvailable() && vehicle.getMake().equalsIgnoreCase(brandName)) {
                System.out.println(vehicle.getVehicleId() + " - " + vehicle.getMake() + " " + vehicle.getModel());
            }
        }
    }

    private void viewAllRentalsOption() {
        System.out.println("\n-- View All Rentals --\n");
        for (Rental rental : rentals) {
            System.out.println("Customer: " + rental.getCustomer().getName() + " | Phone: " +
                    rental.getCustomer().getPhoneNumber() + " | Vehicle: " +
                    rental.getVehicle().getMake() + " " + rental.getVehicle().getModel() + " | Days: " + rental.getDays());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        CarRentalService rentalService = new CarRentalService();

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
