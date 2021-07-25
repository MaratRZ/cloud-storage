package handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import model.AbstractMessage;
import model.FileMessage;
import model.SimpleMessage;

import java.io.FileOutputStream;
import java.io.IOException;

import static model.MessageType.FILE;

@Slf4j
public class FileMessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private static String ROOT_DIR = "server/files";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage msg) {
        if (msg.messageType() == FILE) {
            FileMessage fileMsg = (FileMessage) msg;
            String fileName = fileMsg.getName();
            log.debug("Получен файл " + fileName);
            long fileSize = fileMsg.getSize();
            log.debug("Размер файла " + fileSize);

            try (FileOutputStream fos = new FileOutputStream(ROOT_DIR + "/" + fileName)) {
                fos.write(fileMsg.getContent());
            } catch (IOException e) {
                log.error("", e);
            }
            ctx.writeAndFlush(new SimpleMessage("Файл " + fileName + " загружен на сервер"));
        }
    }
}
