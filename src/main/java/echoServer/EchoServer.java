package echoServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * sever answers back with some message
 */
public class EchoServer {

    private int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void run() throws Exception{

        // NioEventLoopGroup is multithreaded event loop that handles IO operations
        // bossGroup is for accepting incoming connections
        // workerGroup handles the traffic of accepted connection
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // ServerBootstrap is helper class that sets up the server
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup)
                    // we use NioServerSocketChannel to instantiate a new channel to accept incoming connections
                    .channel(NioServerSocketChannel.class)
                    // ChannelInitializer is a special handler that helps a user to configure a new channel
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // we configure channel pipeline of new channel here, also we add some handler
                            // in this case we add our echo server handler
                            socketChannel.pipeline().addLast(new EchoServerHandler());
                        }
                        // we set the channel options here
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // bind and start to accept incoming connections
            ChannelFuture f = server.bind(port).sync();

            f.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0){
            port = Integer.parseInt(args[0]);
        }
        new EchoServer(port).run();
    }

}
