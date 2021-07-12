import javafx.beans.property.*;

public class FileProperty {

    private final StringProperty name;
    private final LongProperty size;

    public FileProperty(String name, Long size) {
        this.name = new SimpleStringProperty(name);
        this.size = new SimpleLongProperty(size);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public LongProperty sizeProperty() {
        return size;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public long getSize() {
        return size.get();
    }

    public void setSize(Long size) {
        this.size.set(size);
    }
}
