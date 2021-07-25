package model;

import lombok.Builder;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage {

    private final String name;
    private final long size;
    private final byte[] content;

    public FileMessage(Path path) throws IOException {
        this.name = path.getFileName().toString();
        this.size = Files.size(path);
        this.content = Files.readAllBytes(path);
    }

    @Override
    public MessageType messageType() {
        return MessageType.FILE;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public byte[] getContent() {
        return content;
    }
}
