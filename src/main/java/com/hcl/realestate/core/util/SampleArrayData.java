package com.hcl.realestate.core.util;

/**
 * Demonstrates Core Java Array fundamentals by modeling one full week (7 days)
 * of real-estate platform transactions, property listings, and daily metrics.
 * 
 * Features:
 * - 1D arrays for daily visits and rent collections
 * - 2D arrays for weekly listing schedules (Day, Title, City, BHK, Rent, Status)
 * - Array traversal, accumulation, and filtering algorithms
 */
public class SampleArrayData {

    // 1D Array: 7 Days of the Week
    public static final String[] DAYS = {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    // 1D Array: Daily rent dues collected over the 7-day period (in INR)
    public static final double[] DAILY_COLLECTIONS = {
        45000.0, 75000.0, 30000.0, 15000.0, 55000.0, 38000.0, 18000.0
    };

    // 1D Array: Site visit requests received each day
    public static final int[] DAILY_VISIT_REQUESTS = {
        3, 5, 2, 4, 6, 8, 5
    };

    // 2D Array: 7 Days of Property Listings
    // Schema: [Day, Title, City, Locality, BHK, Monthly Rent, Status]
    public static final String[][] WEEKLY_LISTINGS = {
        {"Monday", "Prestige Tech Park High-Rise", "Bangalore", "Marathahalli", "3", "45000", Constants.STATUS_APPROVED},
        {"Tuesday", "Sea Breeze Luxury Villa", "Chennai", "ECR", "4", "75000", Constants.STATUS_APPROVED},
        {"Wednesday", "Green Glen Gated Apartment", "Bangalore", "Bellandur", "2", "30000", Constants.STATUS_PENDING_REVIEW},
        {"Thursday", "Cyber City Executive Studio", "Hyderabad", "Hitec City", "1", "15000", Constants.STATUS_APPROVED},
        {"Friday", "Koramangala Premium Penthouse", "Bangalore", "Koramangala", "3", "55000", Constants.STATUS_RENTED},
        {"Saturday", "Marina View High-Rise Flat", "Chennai", "Mylapore", "2", "38000", Constants.STATUS_APPROVED},
        {"Sunday", "Gachibowli Modern Flat", "Hyderabad", "Gachibowli", "1", "18000", Constants.STATUS_PENDING_REVIEW}
    };

    /**
     * Calculates the total rent collections across the 7-day week using array traversal.
     */
    public static double calculateTotalWeeklyCollections() {
        double total = 0.0;
        for (int i = 0; i < DAILY_COLLECTIONS.length; i++) {
            total += DAILY_COLLECTIONS[i];
        }
        return total;
    }

    /**
     * Computes the average daily collection over the 7 days.
     */
    public static double calculateAverageDailyCollection() {
        if (DAILY_COLLECTIONS.length == 0) return 0.0;
        return calculateTotalWeeklyCollections() / DAILY_COLLECTIONS.length;
    }

    /**
     * Sums the total number of site visits requested during the week.
     */
    public static int calculateTotalWeeklyVisits() {
        int total = 0;
        for (int count : DAILY_VISIT_REQUESTS) {
            total += count;
        }
        return total;
    }

    /**
     * Counts how many listings in the 2D array match the specified status.
     */
    public static int countListingsByStatus(String status) {
        int count = 0;
        for (int i = 0; i < WEEKLY_LISTINGS.length; i++) {
            if (WEEKLY_LISTINGS[i][6].equalsIgnoreCase(status)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Displays a formatted tabular report of the 7-day sample data.
     */
    public static void displayWeeklyReport() {
        System.out.println("=========================================================================================");
        System.out.println("            JFS-16 REAL ESTATE SYSTEM - 1 WEEK SAMPLE TRANSACTION DATA                   ");
        System.out.println("=========================================================================================");
        System.out.printf("%-10s | %-32s | %-10s | %-4s | %-8s | %-14s%n",
                "Day", "Property Title", "City", "BHK", "Rent", "Status");
        System.out.println("-----------------------------------------------------------------------------------------");

        for (int i = 0; i < WEEKLY_LISTINGS.length; i++) {
            System.out.printf("%-10s | %-32s | %-10s | %-4s | INR %-4s | %-14s%n",
                    WEEKLY_LISTINGS[i][0],
                    WEEKLY_LISTINGS[i][1],
                    WEEKLY_LISTINGS[i][2],
                    WEEKLY_LISTINGS[i][4],
                    WEEKLY_LISTINGS[i][5],
                    WEEKLY_LISTINGS[i][6]);
        }

        System.out.println("=========================================================================================");
        System.out.printf("Total 7-Day Rent Dues Collected : INR %,.2f%n", calculateTotalWeeklyCollections());
        System.out.printf("Average Daily Collection        : INR %,.2f%n", calculateAverageDailyCollection());
        System.out.printf("Total Site Visit Requests       : %d visits%n", calculateTotalWeeklyVisits());
        System.out.printf("Approved Listings Count         : %d / %d%n",
                countListingsByStatus(Constants.STATUS_APPROVED), WEEKLY_LISTINGS.length);
        System.out.println("=========================================================================================");
    }

    /**
     * Console entry point to demonstrate the sample array data in action.
     */
    public static void main(String[] args) {
        displayWeeklyReport();
    }
}