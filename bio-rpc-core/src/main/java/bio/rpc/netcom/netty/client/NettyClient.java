package bio.rpc.netcom.netty.client;

import bio.rpc.netcom.client.IClient;
import bio.rpc.netcom.protocol.RpcRequest;
import bio.rpc.netcom.protocol.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by luyu on 2017/12/7.
 */
public class NettyClient extends IClient {

    private RpcRequest request;

    private RpcResponse response;

    CountDownLatch latch;

    @Override
    public RpcResponse send(RpcRequest request) throws Exception {
        latch = new CountDownLatch(1);
        this.request = request;
        doConnect();
        latch.await();
        return response;
    }

    private void doConnect() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).
                    option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>(

            ) {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new RpcClientHandler());
                }
            });

            ChannelFuture f = b.connect("127.0.0.1", 7080).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    class RpcClientHandler extends ChannelHandlerAdapter {

        private ByteBuf msg;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws IOException {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bout);
            out.writeObject(request);
            out.flush();
            byte[] arr = bout.toByteArray();
            msg = Unpooled.buffer(arr.length);
            msg.writeBytes(arr);
            ctx.writeAndFlush(msg);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            ByteArrayInputStream bln = new ByteArrayInputStream(req);
            ObjectInputStream in = new ObjectInputStream(bln);
            response = (RpcResponse) in.readObject();
            latch.countDown();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) {
            latch.countDown();
            ctx.close();
        }
    }
}


