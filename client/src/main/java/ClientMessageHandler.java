import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import model.AbstractMessage;

public class ClientMessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private final CallBack callBack;

    public ClientMessageHandler(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage message) {
        callBack.call(message);
    }
}
