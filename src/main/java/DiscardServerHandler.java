import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

//DiscardServerHandler extends ChannelInboundHandlerAdapter,
// which is an implementation of ChannelInboundHandler.
// ChannelInboundHandler provides various event handler methods that you can override.
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    //We override the channelRead() event handler method here.
    // This method is called with the received message,
    // whenever new data is received from a client.
    // In this example, the type of the received message is ByteBuf.
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //  ByteBuf has to be release it is handler responsibility any object passed to the handler
        //A random and sequential accessible sequence of zero or more bytes (octets).
        //  ByteBuf This abstract class provides an abstract view for one or more primitive byte arrays (byte[]) and NIO buffers.
        ByteBuf in = (ByteBuf) msg;
        try {
            while (in.isReadable()){
                System.out.println((char) in.readByte());
                System.out.flush();
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    //The exceptionCaught() event handler method is called with a Throwable
    // when an exception was raised by Netty due to an I/O error
    // or by a handler implementation due to the exception thrown while processing events.
    // In most cases, the caught exception should be logged and its associated channel should be closed
    // here, although the implementation of this method can be different depending on what you want to do
    // to deal with an exceptional situation. For example, you might want to send a response message with an error code
    // before closing the connection.
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
