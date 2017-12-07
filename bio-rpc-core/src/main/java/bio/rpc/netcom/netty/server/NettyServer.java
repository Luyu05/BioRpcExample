package bio.rpc.netcom.netty.server;

import bio.rpc.netcom.NetComServerFactory;
import bio.rpc.netcom.protocol.RpcRequest;
import bio.rpc.netcom.protocol.RpcResponse;
import bio.rpc.netcom.server.IServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by luyu on 2017/12/7.
 */
public class NettyServer extends IServer {

    @Override
    public void start(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //ServerBootstrap 是Netty启动NIO的辅助启动类，旨在降低服务端开发复杂度
            ServerBootstrap b = new ServerBootstrap();
            /**
             * 1.将两个NIO线程组传递到启动类中
             * 2.设置创建的Channel为NioServerSocketChannel
             * 3.配置NioServerSocketChannel的TCP参数，设置backlog为1024？
             * 4.最后绑定I/O事件的处理类
             * */
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel arg0) throws Exception {
                    arg0.pipeline().addLast(new RpcServerHandler());
                }
            });
            ChannelFuture f = b.bind(port).sync();
            //同步阻塞，等待服务器端链路关闭才退出main函数
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void destroy() throws Exception {

    }

    class RpcServerHandler extends ChannelHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            ByteBuf buf = (ByteBuf) msg;
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            ByteArrayInputStream bais = new ByteArrayInputStream(req);
            ObjectInputStream oi = new ObjectInputStream(bais);
            RpcRequest request =(RpcRequest) oi.readObject();

            RpcResponse response = NetComServerFactory.invokeService(request, null);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(response);
            oos.flush();
            byte[] arr = baos.toByteArray();
            ByteBuf message = Unpooled.buffer(arr.length);
            message.writeBytes(arr);
            ctx.writeAndFlush(message);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
            ctx.close();
        }
    }


}
