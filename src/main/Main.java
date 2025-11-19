/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import config.DatabaseConnection;

/**
 *
 * @author gastonlell
 */
public class Main {
    public static void main(String[] args) {
        
        try {
            DatabaseConnection.initializeDatabase();
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Error al crear Base de datos: " + e.getMessage());
        }
        AppMenu menu = new AppMenu();
        menu.run();
    }
}
