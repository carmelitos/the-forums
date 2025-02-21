package me.carmelo.theforums.cli;

import me.carmelo.theforums.repository.RoleRepository;
import me.carmelo.theforums.service.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ConsoleCommandRunner implements CommandLineRunner {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public ConsoleCommandRunner(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Spring Boot CLI is running. Enter commands:");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            String[] parts = input.split(" ");
            String command = parts[0];

            switch (command) {
                case "/superuser" -> {
                    String password = userService.createSuperUser();

                    if(password.equalsIgnoreCase("already exists")) {
                        System.out.println("Super user already exists");
                        return;
                    }

                    System.out.println("Super user created");
                    System.out.println("Username: superuser");
                    System.out.println("Password: " + password);
                    System.out.println("Save the password as this is the last time you will see it");
                }
                default -> System.out.println("Unknown command. Available commands: /user, /roles");
            }
        }

        scanner.close();
        System.out.println("CLI exited.");
    }


}