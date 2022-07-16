package discardServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * discards any incoming data
 */
public class DiscardServer {

    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        // NioEventLoopGroup is a multithreaded event loop that handles IO operations.
        // here we have server side app and therefore two NioEventLoopGroup will be used
        // first called boss accepts incoming connection
        // second often called worker handles the traffic of accepted connection
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // ServerBootstrap is helper class that sets up server
            // we can set up server using a Channel directly, but it is hard process and
            // in most cases we don't need to do this
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    // here we specify to use the NioServerSocketChannel class which is used
                    // to instantiate a new Channel to accept incoming connections.
                    .channel(NioServerSocketChannel.class)
                    // The handler specified here will always be evaluated by a newly accepted Channel.
                    // The ChannelInitializer is a special handler that is purposed to help a user configure a new Channel.
                    // It is most likely that you want to configure the ChannelPipeline of the new Channel by adding some handlers
                    // such as discardServer.DiscardServerHandler to implement your network application.
                    // As the application gets complicated, it is likely that you will add more handlers to the pipeline
                    // and extract this anonymous class into a top-level class eventually.
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new DiscardServerHandler());
                        }
                    })
                    // we can set parameters which are specific to the Channel implementation. here TCP/IP server
                    // we are allowed to set the socket options like tcpNoDelay keepAlive etc.
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // option() is for the NioServerSocketChannel that accepts incoming connections.
                    // childOption() is for the Channels accepted by the parent ServerChannel, which is NioSocketChannel in this case.
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // bind and start to accept incoming connections
            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new DiscardServer(port).run();
    }

}
