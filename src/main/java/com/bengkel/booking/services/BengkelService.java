package com.bengkel.booking.services;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.models.MemberCustomer;
import com.bengkel.booking.models.Vehicle;

public class BengkelService {
	private static Scanner input = new Scanner(System.in);
	
	//infocustomer
	public static void infoCustomer(Customer loggedInCustomer) {
        if (loggedInCustomer == null) {
            System.out.println("Anda belum login. Silakan login terlebih dahulu.");
            return;
        }
        System.out.println("\n========== Customer Profile ==========");
        System.out.println("Customer ID: " + loggedInCustomer.getCustomerId());
        System.out.println("Nama: " + loggedInCustomer.getCustomerName());

        if (loggedInCustomer instanceof MemberCustomer) {
            System.out.println("Customer Status: Member");
            System.out.println("Saldo Koin: " + ((MemberCustomer) loggedInCustomer).getSaldoCoin());
        } else {
            System.out.println("Customer Status: Non Member");
        }

        System.out.println("Alamat: " + loggedInCustomer.getAddress());

        System.out.println("List Kendaraan:");
        System.out.println("+------+--------------+--------------+-----------------+-------+");
        System.out.printf("| %-4s | %-12s | %-12s | %-15s | %-5s |\n", "No.", "Vehicle ID", "Warna", "Tipe Kendaraan", "Tahun");

        List<Vehicle> vehicles = loggedInCustomer.getVehicles();
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);
            System.out.println("+------+--------------+--------------+-----------------+-------+");
            System.out.printf("| %-4s | %-12s | %-12s | %-15s | %-5s |\n", i + 1, vehicle.getVehiclesId(), vehicle.getColor(), vehicle.getVehicleType(), vehicle.getYearRelease());
        }
        System.out.println("+------+--------------+--------------+-----------------+-------+");
    }

    public static void bookService(Customer loggedInCustomer, List<ItemService> listAllItemService) {
        if (loggedInCustomer == null) {
            System.out.println("Anda belum login. Silakan login terlebih dahulu.");
            return;
        }

        System.out.println("\nList Kendaraan Anda:");
        List<Vehicle> customerVehicles = loggedInCustomer.getVehicles();
        for (Vehicle vehicle : customerVehicles) {
            System.out.println("Vehicle ID: " + vehicle.getVehiclesId() + ", Type: " + vehicle.getVehicleType());
        }

        System.out.print("\nMasukkan Vehicle ID yang ingin di-service: ");
        String selectedVehicleId = input.nextLine();

        Vehicle selectedVehicle = null;
        for (Vehicle vehicle : customerVehicles) {
            if (vehicle.getVehiclesId().equalsIgnoreCase(selectedVehicleId)) {
                selectedVehicle = vehicle;
                break;
            }
        }

        if (selectedVehicle == null) {
            System.out.println("Kendaraan dengan Vehicle ID " + selectedVehicleId + " tidak ditemukan.");
            return;
        }

    
        String vehicleType = selectedVehicle.getClass().getSimpleName();
        System.out.println("|--------------------------------------------------------------------------------");
        System.out.println(String.format("| %-4s | %-11s | %-20s | %-15s | %-15s |", "No", "Service ID", "Nama Service", "Tipe Kendaraan", "Harga"));
        System.out.println("|--------------------------------------------------------------------------------");

        int count = 1;

        for (ItemService itemService : listAllItemService) {
            if (itemService.getApplicableVehicleType().equalsIgnoreCase(vehicleType)) {
                System.out.println(String.format("| %-4d | %-11s | %-20s | %-15s | %-15s |",
                        count++,
                        itemService.getServiceId(),
                        itemService.getServiceName(),
                        itemService.getApplicableVehicleType(),
                        formatCurrency(itemService.getPrice()))); 
            }
        }
        System.out.println("|--------------------------------------------------------------------------------");

        // Memilih service item
        int maxNumberOfService = (loggedInCustomer instanceof MemberCustomer) ? 2 : 1;
        for (int serviceCount = 1; serviceCount <= maxNumberOfService; serviceCount++) {
            System.out.print("Masukkan Service ID yang ingin dipilih (Service " + serviceCount + "): ");
            String selectedServiceId = input.nextLine();

            ItemService selectedService = findServiceById(selectedServiceId, listAllItemService);

            if (selectedService == null) {
                System.out.println("Service dengan Service ID " + selectedServiceId + " tidak ditemukan.");
                return;
            }

            // Menambahkan service ke list service customer
            loggedInCustomer.addService(selectedService);

            if (serviceCount < maxNumberOfService) {
                System.out.print("Apakah anda ingin menambahkan Service Lainnya? (Y/T): ");
                String addMoreServiceChoice = input.nextLine();

                if (!addMoreServiceChoice.equalsIgnoreCase("Y")) {
                    break;
                }
            }
        }

        System.out.println("Pilih Metode Pembayaran:");
        if (loggedInCustomer instanceof MemberCustomer) {
            System.out.println("1. Saldo Coin");
            System.out.println("2. Cash");
        } else {
            System.out.println("1. Cash");
        }

        int paymentChoice = Validation.validasiNumberWithRange("Masukkan pilihan metode pembayaran: ", "Input harus berupa angka!",
                "^[0-9]+$", (loggedInCustomer instanceof MemberCustomer) ? 2 : 1, 1);

        BengkelService bengkelService = new BengkelService();

        double totalPayment = bengkelService.calculateTotalPayment(loggedInCustomer.getBookedServices());
        double totalharga=totalPayment;
        if (loggedInCustomer instanceof MemberCustomer && paymentChoice == 1) {
            
            totalPayment *= 0.9;

                double saldoCoin = ((MemberCustomer) loggedInCustomer).getSaldoCoin();
                if (saldoCoin >= totalPayment) {
                    ((MemberCustomer) loggedInCustomer).setSaldoCoin(saldoCoin - totalPayment);
                } else {
                    System.out.println("Saldo Coin tidak mencukupi. Silakan pilih metode pembayaran lain.");
                    return;
                }
            }

        // Menampilkan informasi booking
        System.out.println("\nBooking berhasil!");
        System.out.println("Kendaraan: " + selectedVehicle.getVehicleType() + " dengan Vehicle ID: " + selectedVehicle.getVehiclesId());
        System.out.println("Total Harga: " + totalharga);
        System.out.println("Total Pembayaran: " + totalPayment);

        if (loggedInCustomer instanceof MemberCustomer) {
            System.out.println("Sisa Saldo Coin: " + ((MemberCustomer) loggedInCustomer).getSaldoCoin());
        }   
    }

	private static void displayItemServicesByVehicleType(List<ItemService> listAllItemService, String vehicleType) {
        for (ItemService itemService : listAllItemService) {
            if (itemService.getApplicableVehicleType().equalsIgnoreCase(vehicleType)) {
                System.out.println("Service ID: " + itemService.getServiceId() + ", Nama: " +
                        itemService.getServiceName() + ", Harga: " + itemService.getPrice());
            }
        }
    }

    private static ItemService findServiceById(String serviceId, List<ItemService> listAllItemService) {
        for (ItemService itemService : listAllItemService) {
            if (itemService.getServiceId().equalsIgnoreCase(serviceId)) {
                return itemService;
            }
        }
        return null;
    }
	
	//Top Up Saldo Coin Untuk Member Customer
	public static void topUpCoin(Customer loggedInCustomer) {
        if (loggedInCustomer instanceof MemberCustomer) {
            MemberCustomer memberCustomer = (MemberCustomer) loggedInCustomer;
            
            System.out.println("Saldo Coin sebelum top up: " + memberCustomer.getSaldoCoin());

            double amountToTopUp = getAmountToTopUp(); 
            memberCustomer.topUpCoin(amountToTopUp);
            System.out.println("Saldo Coin setelah top up: " + memberCustomer.getSaldoCoin());
        } else {
            System.out.println("Maaf, fitur ini hanya untuk Member saja!");
        }
    }
    private static double getAmountToTopUp() {
        System.out.print("Masukkan jumlah top up: ");
        return input.nextDouble();
    }
	
	//Metod informasi booking
	public static void displayBookingInfo(Customer loggedInCustomer) {
        if (loggedInCustomer.getBookedServices().isEmpty()) {
            System.out.println("Belum ada Booking Order yang dilakukan.");
        } else {
            System.out.println("========Booking Order Menu========");

        List<ItemService> bookedServices = loggedInCustomer.getBookedServices();
			double totalPayment = 0.0;
			for (ItemService bookedService : bookedServices) {
				totalPayment += bookedService.getPrice();
			}

            System.out.println(String.format("| %-4s | %-20s | %-15s | %-15s | %-15s | %-15s | %-15s |", "No", "Booking ID", "Nama Customer", "Payment Method", "Total Service", "Total Payment", "List Service"));
			System.out.println("--------------------------------------------------------------------------------------------------------------------");

			int count = 1;
			for (ItemService bookedService : bookedServices) {
				String bookingId = generateBookingId();
				String customerName = loggedInCustomer.getCustomerName();
				String paymentMethod = getPaymentMethod();
				double totalService = bookedService.getPrice();
				String listService = bookedService.getServiceName();

				System.out.printf("| %-4s | %-20s | %-15s | %-15s | %-15s | %-15s | %-15s |\n",
						count++,
						bookingId,
						customerName,
						paymentMethod,
						(formatCurrency(totalService)),
						(formatCurrency(totalPayment)),
						listService);
			}
			System.out.println("--------------------------------------------------------------------------------------------------------------------");
		}
    }

    private static String generateBookingId() {
        return "Bkg-" + System.currentTimeMillis();
    }

    private static String getPaymentMethod() {
        return "Cash";
    }

    public double calculateTotalPayment(List<ItemService> bookedServices) {
        double totalPayment = 0.0;

        for (ItemService bookedService : bookedServices) {
            totalPayment += bookedService.getPrice();
        }

        return totalPayment;
    }

    private static String getCurrentDate() {
        return "2024-01-09";
    }

    public static String formatCurrency(double formatCurrency) {
        Locale indonesiaLocale = new Locale("id", "ID");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(indonesiaLocale);
        return currencyFormat.format(formatCurrency);
    }
	
}
