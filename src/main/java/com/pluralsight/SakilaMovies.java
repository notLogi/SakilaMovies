package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class SakilaMovies {
    public static void main(String[] args) throws ClassNotFoundException {
        if(args.length != 2){
            System.out.println("Application needs two arguments to run.");
            System.exit(1);
        }
        String username = args[0];
        String password = args[1];
        Scanner scanner = new Scanner(System.in);

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        System.out.println("Enter the last name of an actor you like: ");
        String lastNameFirstPrompt = scanner.nextLine();
        displayActorsOnLastName(dataSource, lastNameFirstPrompt);
        System.out.println("To see what movies an actor is in, enter the first and last name of an actor you like");
        System.out.println("Enter last name");
        String lastNameSecondPrompt = scanner.nextLine();
        System.out.println("Enter first name: ");
        String firstNameSecondPrompt = scanner.nextLine();
        displayActorsInMovie(dataSource, lastNameSecondPrompt, firstNameSecondPrompt);
        scanner.close();
    }

    private static void displayActorsOnLastName(BasicDataSource dataSource, String lastNameFirstPrompt){
        try{
            String query = """
                    SELECT * FROM actor
                    WHERE last_name = ?;
                    """;
            try(Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1, lastNameFirstPrompt);
                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    boolean found = false;
                    while(resultSet.next()){
                        int actorId = resultSet.getInt("actor_id");
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("last_name");
                        if (!firstName.isEmpty()) found = true;
                        System.out.println("Actor ID: " + actorId + "\nActor last name: " + lastName + "\nActor first name: " + firstName + "\n========================");
                    }
                    if(!found) System.out.println("No queries matched.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void displayActorsInMovie(BasicDataSource dataSource, String lastName, String firstName){
        try{
            String query = """
                    SELECT *, film.title FROM film_actor
                    JOIN film ON film.film_id = film_actor.film_id
                    JOIN actor ON film_actor.actor_id = actor.actor_id
                    WHERE last_name = ? AND first_name = ?;
                    """;
            try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1, lastName);
                preparedStatement.setString(2, firstName);
                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    boolean found = false;
                    while(resultSet.next()){
                        String filmTitle = resultSet.getString("film.title");
                        if(!filmTitle.isEmpty()) found = true;
                        System.out.println("Film title: " + filmTitle + "\n=====================");
                    }
                    if(!found) System.out.println("No queries have been found");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
