package timeServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class timeClient {

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 8080;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // similar to ServerBootstrap but it is for non-server channels - client side
            b.group(workerGroup); // when we specify only one group it will be used as a boss group and a worker group
            b.channel(NioSocketChannel.class); // NioSocketChannel is used to create a client-side channel
            b.option(ChannelOption.SO_KEEPALIVE, true); // we don't use childOption() here cos client-side SocketChannel does not have a parent
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new TimeClientHandler());
                }
            });
            // start the client
            // we use connect() instead of bind()
            ChannelFuture f = b.connect(host, port).sync();
            // wait until the connection is closed
            f.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
