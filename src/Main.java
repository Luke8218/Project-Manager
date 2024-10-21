import java.util.Scanner;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Arrays;

public class Main {

    /*
     * ANSI Escape Codes for terminal printing purposes
     */
    public static final String COLOUR_GREEN = "\u001B[32m";
    public static final String COLOUR_RED = "\u001B[31m";
    public static final String COLOUR_RESET = "\u001B[0m";
    public static final String SCREEN_CLEAR = "\033[H\033[2J";
    public static void main(String[] args) throws Exception {
        start();
    }

    /*
     * Asks the user to select a menu option from the below. While loop used to
     * ensure the user only enters a valid input. Program will repeat the question
     * if an invalid input is entered.
     */
    private static void start() {
        System.out.println(COLOUR_GREEN + "\n\n------ Project Management -------" + COLOUR_RESET);
        System.out.println("Please select an option:");
        System.out.println("1: Create a new project");
        System.out.println("2: View projects");
        System.out.println("3. Close program\n");

        Scanner scanner = new Scanner(System.in);
        String selection = scanner.nextLine();
        List<String> validOptions = Arrays.asList("1", "2", "3");

        while(!validOptions.contains(selection)) {
            System.out.println(COLOUR_RED + "Invalid option. Please try again." + COLOUR_RESET);
            selection = scanner.nextLine();
        }

        switch (selection) {
            case "1":
                createProject();
                break;
            case "2":
                viewProjects();
                break;
            case "3":
                System.exit(0);
                break;
            default:
                break;
        }

        scanner.close();
    }

    /*
     * Creates a new project by prompting the user for the project title, goal, and
     * start date. It then creates a new Project object and saves it to disk using
     * the FileManager class.
     */
    private static void createProject() {
        Project project = new Project();
        Scanner scanner = new Scanner(System.in);

        System.out.println(COLOUR_GREEN + "\n------- New Project Creation -------" + COLOUR_RESET);
        System.out.println(project.id + "\n");

        System.out.print("Project Title: ");
        project.title = scanner.nextLine();

        System.out.print("Project Goal: ");
        project.goal = scanner.nextLine();

        // While loop is used to ensure a valid date (in dd/MM/yyyy format) is entered.
        boolean dateIsValid = false;
        while (!dateIsValid) {
            System.out.println("Project Start Date (dd/MM/yyyy)");
            String projectStartDate = scanner.nextLine();

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                project.startDate = dateFormat.parse(projectStartDate);
                dateIsValid = true;
            } catch (ParseException e) {
                System.out.println("Invalid date format.");
            }
        }

        FileManager fileManager = new FileManager();
        fileManager.createProjectFolderStructure(project.id);

        try {
            fileManager.createProjectConfigFile(project);
        } catch (IOException e) {
            System.out.println(COLOUR_RED + "Error creating project config file: " + e.getMessage() + COLOUR_RESET);
            start();
            scanner.close();
        }

        clearScreen();

        System.out.println(COLOUR_GREEN + "Project Created Successfully" + COLOUR_RESET);
        System.out.println("Title: " + project.title);
        System.out.println("Goal: " + project.goal);
        System.out.println("Start Date: " + new SimpleDateFormat("dd/MM/yyyy").format(project.startDate));

        newProjectOptions(project);
    }

    /*
     * Displays a list of options for the user to select from after creating a new
     * project. These options include adding an individual or meeting, or returning to
     * the home screen.
     */
    public static void newProjectOptions(Project project) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nPlease select an option:");
        System.out.println("1: Add an individual");
        System.out.println("2: Add a meeting");
        System.out.println("3. Go to home\n");

        String selection = scanner.nextLine();
        List<String> validOptions = Arrays.asList("1", "2", "3");

        while(!validOptions.contains(selection)) {
            System.out.println("Invalid option. Please try again.");
            selection = scanner.nextLine();
        }

        switch (selection) {
            case "1":
                createNewIndividual(project);
                break;
            case "2":
                createNewMeeting(project);
                break;
            case "3":
                clearScreen();
                start();
                break;
            default:
                break;
        }

        scanner.close();
    }

    /*
     * Creates a new individual by prompting the user for the individual's name and
     * role. It then creates a new Individual object and saves it to disk using the
     * FileManager class.
     */
    public static void createNewIndividual(Project project) {
        Individual individual = new Individual();
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n------- New Individual -------");
        System.out.println(individual.id);
        System.out.println("Project: " + project.title + "\n");

        System.out.print("Name: ");
        individual.name = scanner.nextLine();

        System.out.print("Role: ");
        individual.role = scanner.nextLine();

        FileManager fileManager = new FileManager();

        try {
            fileManager.createIndividualFile(project, individual);
            project.individuals.add(individual);
        } catch (IOException e) {
            System.out.println(COLOUR_RED + "Error creating individual file: " + e.getMessage() + COLOUR_RESET);
            start();
            scanner.close();
        }

        System.out.println(COLOUR_GREEN + "Individual Added Successfully" + COLOUR_RESET);
        System.out.println("Name: " + individual.name);
        System.out.println("Role: " + individual.role + "\n\n");

        newProjectOptions(project);

        scanner.close();
    }

    /*
     * Creates a new meeting by prompting the user for the meeting's title, date,
     * summary and attendees. It then creates a new Meeting object and saves it to disk
     * using the FileManager class.
     */
    public static void createNewMeeting(Project project) {
        Meeting meeting = new Meeting();
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n------- New Meeting -------");
        System.out.println(meeting.id);
        System.out.println("Project: " + project.title + "\n");

        System.out.print("Title: ");
        meeting.title = scanner.nextLine();

        boolean dateIsValid = false;
        while (!dateIsValid) {
            System.out.print("Date (dd/MM/yyyy) ");
            String meetingStartDate = scanner.nextLine();

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                meeting.date = dateFormat.parse(meetingStartDate);
                dateIsValid = true;
            } catch (ParseException e) {
                System.out.println("Invalid date format.");
            }
        }

        System.out.print("Summary: ");
        meeting.summary = scanner.nextLine();

        /*
         * Checks to see if there are any individuals assigned to the project. If so,
         * it displays them and prompts the user to select which individuals will be
         * attending the meeting.
         */
        System.out.println("Attendees: \n");
        if (project.individuals == null || project.individuals.size() == 0) {
            System.out.println(COLOUR_RED + "\nNo individuals have been assigned to this project. Moving on.." + COLOUR_RESET);
        } else {
            for (int i = 0; i < project.individuals.size(); i++) {
                System.out.println("    " + (i+1) + ": " + project.individuals.get(i).name + " (" + project.individuals.get(i).role + ")");
            }

            System.out.print("\nPlease enter the numbers of the attendees for this meeting (comma separated, empty for none): ");
        
            String attendeesResponse = scanner.nextLine();
            if  (attendeesResponse.isBlank()) {
                System.out.println(COLOUR_RED + "\nNo individuals have been assigned to this project. Moving on.." + COLOUR_RESET);
            } else {
                String[] attendees = attendeesResponse.split(",");
                for (String attendee : attendees) {
                    try {
                        meeting.attendees.add(project.individuals.get(Integer.parseInt(attendee) - 1));
                        System.out.println("Added " + project.individuals.get(Integer.parseInt(attendee) - 1).name + " to meeting");
                    } catch (NumberFormatException e) {
                        System.out.println(COLOUR_RED + "Invalid attendee number: " + attendee + COLOUR_RESET);
                    }
                }
            }
        }
        
        FileManager fileManager = new FileManager();

        try {
            fileManager.createMeetingFile(project, meeting);
        } catch (IOException e) {
            System.out.println(COLOUR_RED + "Error creating meeting file: " + e.getMessage() + COLOUR_RESET);
            start();
            scanner.close();
        }

        System.out.println(COLOUR_GREEN + "\nMeeting Added Successfully" + COLOUR_RESET);
        System.out.println("Title: " + meeting.title);
        System.out.println("Date: " + new SimpleDateFormat("dd/MM/yyyy").format(meeting.date));
        System.out.println("Summary: " + meeting.summary);
        System.out.println("Attendees: " + String.join(", ", meeting.attendees.stream().map(individual -> individual.name).toList()) + "\n");

        newProjectOptions(project);

        scanner.close();
    }

    /*
     * Displays a list of all projects stored in the projects folder. It then displays
     * the details of each project, including the title, goal, start date, individuals,
     * and meetings.
     */
    public static void viewProjects() {

        clearScreen();

        List<Project> projects = new FileManager().getProjects();

        if (projects.isEmpty()) {
            System.out.println(COLOUR_RED + "No Projects Found. Create a new project using option 1" + COLOUR_RESET);
        } else {
            for (Project project : projects) {
                System.out.println(COLOUR_GREEN + project.title + COLOUR_RESET);
                System.out.println("Goal: " + project.goal);
                System.out.println("Start Date: " + new SimpleDateFormat("dd/MM/yyyy").format(project.startDate));
                System.out.println("Individuals:");
                for (int i = 0; i < project.individuals.size(); i++) {
                    System.out.println("    " + (i+1) + ": " + project.individuals.get(i).name + " - " + project.individuals.get(i).role);
                }
                System.out.println("Meetings:");
                for (int i = 0; i < project.meetings.size(); i++) {
                    System.out.println("    " + (i+1) + ": " + project.meetings.get(i).title + " - " + new SimpleDateFormat("dd/MM/yyyy").format(project.meetings.get(i).date));
                    System.out.println("       Summary: " + project.meetings.get(i).summary);
                    System.out.println("       Attendees: " + String.join(", ", project.meetings.get(i).attendees.stream().map(individual -> individual.name).toList()));
                }
                System.out.println("\n\n");
            }
        }

        start();

    }

    /*
     * Clears the console screen using ANSI escape codes.
     */
    public static void clearScreen() {  
        System.out.print(SCREEN_CLEAR);  
        System.out.print(SCREEN_CLEAR);  
        System.out.flush();  
    }  
}