package bio.rpc.netcom.nio.client;

import bio.rpc.netcom.protocol.RpcRequest;
import bio.rpc.netcom.protocol.RpcResponse;
import bio.rpc.netcom.client.IClient;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by luyu on 2017/11/21.
 */
public class NioClient extends IClient {

    private Selector selector;
    private SocketChannel socketChannel;
    private boolean stop;
    private RpcRequest request;

    public NioClient() {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public RpcResponse send(RpcRequest request) throws Exception {

        this.request = request;
        doConnect();
        RpcResponse response = null;

        while (!stop) {
            selector.select(1000);
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()) {
                SelectionKey sk = it.next();
                if (sk.isValid()) {
                    SocketChannel sc = (SocketChannel) sk.channel();
                    if (sk.isConnectable()) {
                        if (sc.finishConnect()) {
                            sc.register(selector, SelectionKey.OP_READ);
                            doWrite(sc);
                        } else {
                            System.exit(1);
                        }
                    }
                    if (sk.isReadable()) {
                        ByteBuffer bbln = ByteBuffer.allocate(1024);
                        sc.read(bbln);
                        sc.close();
                        ByteArrayInputStream bln = new ByteArrayInputStream(bbln.array());
                        ObjectInputStream in = new ObjectInputStream(bln);
                        response = (RpcResponse) in.readObject();
                        this.stop = true;

                    }
                }
                it.remove();
            }
        }
        if(selector != null){
            try {
                selector.close();
            }catch (Exception e){

            }
        }
        return response;
    }

    private void doConnect() throws Exception {

        if(!selector.isOpen()){
            stop = false;
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        }

        if (socketChannel.connect(new InetSocketAddress("127.0.0.1", 7080))) {
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        } else {
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel socketChannel) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(request);
        out.flush();
        byte[] arr = bout.toByteArray();
        ByteBuffer bb = ByteBuffer.wrap(arr);
        out.close();
        socketChannel.write(bb);
    }

}
