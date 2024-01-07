package com.bengkel.booking.services;

import java.util.List;
import java.util.Scanner;

import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.models.MemberCustomer;
import com.bengkel.booking.models.Vehicle;
import com.bengkel.booking.repositories.CustomerRepository;
import com.bengkel.booking.repositories.ItemServiceRepository;

public class MenuService {
	private static List<Customer> listAllCustomers = CustomerRepository.getAllCustomer();
	private static List<ItemService> listAllItemService = ItemServiceRepository.getAllItemService();
	private static Scanner input = new Scanner(System.in);
	private static int loginAttempt = 0;
	private static Customer loggedInCustomer = null;

    public static void run() {
        boolean isLooping = true;

        do {
            displayInitialMenu();
            int choice = Validation.validasiNumberWithRange("Masukkan Pilihan Menu:", "Input Harus Berupa Angka!", "^[0-1]$", 1, 0);

            switch (choice) {
                case 1:
                    if (login()) {
                        mainMenu();
                    } else {
                        isLooping = handleLoginFailure();
                    }
                    break;
                case 0:
                    System.out.println("Terima kasih! Program berhenti.");
                    isLooping = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        } while (isLooping);
    }

    private static void displayInitialMenu() {
        System.out.println("========== Selamat Datang di Aplikasi Booking Bengkel ==========");
        System.out.println("1. Login");
        System.out.println("0. Exit");
    }

    private static boolean login() {
	System.out.println("\n========== Login ==========");
    System.out.print("Masukkan Customer ID: ");
    String customerId = input.nextLine();
    System.out.print("Masukkan Password: ");
    String password = input.nextLine();

    loggedInCustomer = null;

    for (Customer customer : listAllCustomers) {
        if (customer.getCustomerId().equals(customerId)) {
            loggedInCustomer = customer;
            break;
        }
    }

    if (loggedInCustomer == null) {
        System.out.println("Customer ID Tidak Ditemukan atau Salah!");
        return false;
    }

    if (!loggedInCustomer.getPassword().equals(password)) {
        System.out.println("Password yang Anda Masukkan Salah!");
        loggedInCustomer = null;
        return false;
    }
    return true;
}


    private static boolean handleLoginFailure() {
        loginAttempt++;

        if (loginAttempt >= 3) {
            System.out.println("Anda telah melebihi batas maksimal percobaan login. Aplikasi berhenti.");
            return false;
        } else {
            System.out.println("Gagal login. Silakan coba lagi.");
            return true;
        }
    }

	public static void mainMenu() {
        String[] listMenu = {"Informasi Customer", "Booking Bengkel", "Top Up Bengkel Coin", "Informasi Booking", "Logout"};
        int menuChoice = 0;
        boolean isLooping = true;
    
        outerLoop: do {
            PrintService.printMenu(listMenu, "\n========== Selamat Datang di Aplikasi Booking Bengkel ==========");
            menuChoice = Validation.validasiNumberWithRange("Masukan Pilihan Menu:", "Input Harus Berupa Angka!", "^[0-9]+$", listMenu.length - 1, 0);
            System.out.println(menuChoice);
    
            switch (menuChoice) {
                case 1:
                    BengkelService.infoCustomer(loggedInCustomer);
                    break;
                case 2:
                    BengkelService.bookService(loggedInCustomer, listAllItemService);
                    break;
                case 3:
                    BengkelService.topUpCoin(loggedInCustomer);
                    break;
                case 4:
                    BengkelService.displayBookingInfo(loggedInCustomer);
                    break;
                case 0:
                    System.out.println("Terima kasih! Program berhenti.");
                    isLooping = false;
                    break outerLoop; 
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        } while (isLooping);
    }
}
