package timeServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    // channelActive will be invoked wen a connection is established
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        //we allocate a new buffer wich will contain the message.
        // we are going to write a 32 bit integer, therefore we use byteBuf whose capacity is at least 4 bytes;
        final ByteBuf time = ctx.alloc().buffer(4);
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

        // channelFuture represents an IO operation which has not occurred yet
        // we need to call close() after channelfuture is complete, which was returned by write()
        // and it notifies its listeners when the operation has been done
        final ChannelFuture f = ctx.writeAndFlush(time);

        // notify when a write request is finished. we add a ChannelFutureListener to the returned futureChannel
        // here we create anonymous ChannelFutureListener which closes the channel when the operation is done
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                assert f == channelFuture;
                ctx.close();
            }
        });


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
