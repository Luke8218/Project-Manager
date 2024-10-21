import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Meeting {
    public UUID id;
    public String title;
    public Date date;
    public String summary;
    public List<Individual> attendees = new ArrayList<Individual>();

    public Meeting() {
        id = UUID.randomUUID();
    }
}