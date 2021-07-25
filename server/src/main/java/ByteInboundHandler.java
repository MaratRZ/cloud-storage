import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ByteInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("Client accepted..");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.debug("received: {}", msg);
        ByteBuf buf = (ByteBuf) msg;

        StringBuilder s = new StringBuilder();
        while (buf.isReadable()){
            char b = (char) buf.readByte();
            s.append(b);
        }
        log.debug("msg: {}", s);

        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes(s.toString().getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(buffer);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("Client disconnected..");
    }
}
