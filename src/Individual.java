import java.util.UUID;

class Individual {
    public UUID id;
    public String name;
    public String role;

    public Individual() {
        id = UUID.randomUUID();
    }
}