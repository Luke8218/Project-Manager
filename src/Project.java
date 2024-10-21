import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Project {
    public UUID id;
    public String title;
    public String goal;
    public Date startDate; 
    public List<Individual> individuals = new ArrayList<Individual>(); // Who is working on the project
    public List<Meeting> meetings = new ArrayList<Meeting>();

    public Project() {
        id = UUID.randomUUID();
    }
}