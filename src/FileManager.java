import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
public class FileManager {

    private static final String BASE_PROJECT_PATH = "projects";

    /*
     * Creates the project folder structure, including the config file, individuals
     * folder, and meetings folder.
     */
    public void createProjectFolderStructure(UUID projectId) {
        File projectFolder = new File(BASE_PROJECT_PATH, projectId.toString());
        projectFolder.mkdirs();
        createIndividualsFolder(projectId);
        createMeetingsFolder(projectId);
    }

    /*
     * Creates the project config file, which stores the project title, goal, and
     * start date.
     */
    public void createProjectConfigFile(Project project) throws IOException {
        File projectConfigFile = new File(BASE_PROJECT_PATH + "/" + project.id.toString(), "config.txt");
        projectConfigFile.createNewFile();
        setupProjectConfigFile(project);
    }

    /*
     * Writes the project details to the project config file.
     */
    private void setupProjectConfigFile(Project project) throws IOException {
        FileWriter writer = new FileWriter(BASE_PROJECT_PATH + "/" + project.id.toString() + "/config.txt");
        writer.append("id:" + project.id);
        writer.append("\ntitle:" + project.title);
        writer.append("\ngoal:" + project.goal);
        writer.append("\nstartDate:" + new SimpleDateFormat("dd/MM/yyyy").format(project.startDate));
        writer.close();
    }

    /*
     * Creates the individuals folder within the project folder.
     */
    private void createIndividualsFolder(UUID projectId) {
        File projectFolder = new File(BASE_PROJECT_PATH + "/" + projectId.toString(), "individuals");
        projectFolder.mkdirs();
    }

    /*
     * Creates the meetings folder within the project folder.
     */
    private void createMeetingsFolder(UUID projectId) {
        File projectFolder = new File(BASE_PROJECT_PATH + "/" + projectId.toString(), "meetings");
        projectFolder.mkdirs();
    }

    /*
     * Creates an individual file within the project's individuals folder.
     */
    public void createIndividualFile(Project project, Individual individual) throws IOException {
        File individualFile = new File(BASE_PROJECT_PATH + "/" + project.id.toString() + "/individuals", individual.id.toString() + ".txt");
        individualFile.createNewFile();
        setupIndividualFile(project, individual);
    }

    /*
     * Writes the individual details to the individual file.
     */
    private void setupIndividualFile(Project project, Individual individual) throws IOException {
        FileWriter writer = new FileWriter(BASE_PROJECT_PATH + "/" + project.id.toString() + "/individuals/" + individual.id.toString() + ".txt");
        writer.append("id:" + individual.id);
        writer.append("\nname:" + individual.name);
        writer.append("\nrole:" + individual.role);
        writer.close();
    }

    /*
     * Creates a meeting file within the project's meetings folder.
     */
    public void createMeetingFile(Project project, Meeting meeting) throws IOException {
        File meetingFile = new File(BASE_PROJECT_PATH + "/" + project.id.toString() + "/meetings", meeting.id.toString() + ".txt");
        meetingFile.createNewFile();
        setupMeetingFile(project, meeting);
    }

    /*
     * Writes the meeting details to the meeting file.
     */
    private void setupMeetingFile(Project project, Meeting meeting) throws IOException {
        FileWriter writer = new FileWriter(BASE_PROJECT_PATH + "/" + project.id.toString() + "/meetings/" + meeting.id.toString() + ".txt");
        writer.append("id:" + meeting.id);
        writer.append("\ntitle:" + meeting.title);
        writer.append("\ndate:" + new SimpleDateFormat("dd/MM/yyyy").format(meeting.date));
        writer.append("\nsummary:" + meeting.summary);

        List<String> attendeeIds = new ArrayList<>();
        for (Individual individual : meeting.attendees) {
            attendeeIds.add(individual.id.toString());
        }

        writer.append("\nattendees:" + String.join(",", attendeeIds));
        writer.close();
    }

    /*
     * Returns a list of all projects by reading the project config files.
     */
    public List<Project> getProjects() {
        List<Project> projects = new ArrayList<>();
        File projectFolder = new File(BASE_PROJECT_PATH);
        File[] projectFolders = projectFolder.listFiles();
        if (projectFolders != null) { // Check to see if there are any project folders
            for (File projectFile : projectFolders) { 
                if (projectFile.isDirectory()) {
                    File config = new File(projectFile.getAbsolutePath() + "/config.txt");
                    if (config.exists()) {
                        try {
                            projects.add(getProjectFromConfig(config));
                        } catch (FileNotFoundException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return projects;
    }

    /*
     * Returns a project object by reading the project config file.
     */
    private Project getProjectFromConfig(File config) throws FileNotFoundException, ParseException {
        
        /*
         * Config File Layout
         * id:<id>
         * title:<title>
         * goal:<goal>
         * startDate:<startDate>
         */

        Scanner scanner = new Scanner(config);
        Project project = new Project();
        
        project.id = UUID.fromString(scanner.nextLine().split(":")[1]);
        project.title = scanner.nextLine().split(":")[1];
        project.goal = scanner.nextLine().split(":")[1];
        project.startDate = new SimpleDateFormat("dd/MM/yyyy").parse(scanner.nextLine().split(":")[1]);
        scanner.close();

        project.individuals = getProjectIndividuals(project);
        project.meetings = getProjectMeetings(project);

        return project;
        
    }

    /*
     * Returns a list of all individuals within a project by reading the individual
     * files.
     */
    private List<Individual> getProjectIndividuals(Project project) {
        List<Individual> individuals = new ArrayList<>();
        File individualsFolder = new File(BASE_PROJECT_PATH + "/" + project.id.toString() + "/individuals");
        File[] individualFiles = individualsFolder.listFiles();
        if (individualFiles != null) {
            for (File individualFile : individualFiles) {
                try {
                    individuals.add(getIndividualFromConfig(individualFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return individuals;
    }

    /*
     * Returns an individual object by reading the individual file.
     */
    private Individual getIndividualFromConfig(File individualFile) throws FileNotFoundException {
        /*
         * Individual File Layout
         * id:<id>
         * name:<name>
         * role:<role>
         */

         Scanner scanner = new Scanner(individualFile);
         Individual individual = new Individual();
         
         individual.id = UUID.fromString(scanner.nextLine().split(":")[1]);
         individual.name = scanner.nextLine().split(":")[1];
         individual.role = scanner.nextLine().split(":")[1];
         scanner.close();
 
         return individual;
    }

    /*
     * Returns a list of all meetings within a project by reading the meeting files.
     */
    private List<Meeting> getProjectMeetings(Project project) {
        List<Meeting> meetings = new ArrayList<>();
        File meetingsFolder = new File(BASE_PROJECT_PATH + "/" + project.id.toString() + "/meetings");
        File[] meetingFiles = meetingsFolder.listFiles();
        if (meetingFiles != null) {
            for (File meetingFile : meetingFiles) {
                try {
                    meetings.add(getMeetingFromConfig(meetingFile, project));
                } catch (FileNotFoundException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return meetings;
    }

    /*
     * Returns a meeting object by reading the meeting file.
     */
    private Meeting getMeetingFromConfig(File meetingFile, Project project) throws FileNotFoundException, ParseException {
        /*
         * Meeting File Layout
         * id:<id>
         * title:<title>
         * date:<date>
         * summary:<summary>
         * attendees:<attendees> (comma separated)
         */

         Scanner scanner = new Scanner(meetingFile);
         Meeting meeting = new Meeting();
         
         meeting.id = UUID.fromString(scanner.nextLine().split(":")[1]);
         meeting.title = scanner.nextLine().split(":")[1];
         meeting.date = new SimpleDateFormat("dd/MM/yyyy").parse(scanner.nextLine().split(":")[1]);
         meeting.summary = scanner.nextLine().split(":")[1];
         meeting.attendees = getMeetingAttendees(scanner.nextLine().split(":")[1], project);
         scanner.close();
 
         return meeting;
    }

    /*
     * Returns a list of all attendees within a meeting by reading the attendee ids.
     */
    private List<Individual> getMeetingAttendees(String attendeeIds, Project project) {
        List<String> attendeeIdsList = Arrays.asList(attendeeIds.split(","));
        List<Individual> individuals = new ArrayList<>();
        File individualsFolder = new File(BASE_PROJECT_PATH + "/" + project.id.toString() + "/individuals");
        File[] individualFiles = individualsFolder.listFiles();
        if (individualFiles != null) {
            for (File individualFile : individualFiles) {
                try {
                    Individual attendeeFromConfigFile = getIndividualFromConfig(individualFile);
                    if (attendeeIdsList.contains(attendeeFromConfigFile.id.toString())) {
                        individuals.add(attendeeFromConfigFile);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return individuals;
    }
}
