package echoServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Random;

// we extend ChannelInboundHandlerAdapter which provides various event handler methods that we can override.
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    // this method is being called with the received message, whenever new data is received from a client.
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // ctx.write(object) doesn't make the message written out to the wire.
        // It is buffered internally and the flushed out to the wire by ctx.flush()
        ctx.write(msg);
        ctx.flush();
    }

    // this method os called when we have exception caused by netty or
    // handler implementation due to exception thrown while processing events.
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
