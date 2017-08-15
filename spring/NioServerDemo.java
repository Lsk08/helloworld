public class NioServerDemo {
    //overall flow   1. create a server【ServerSocketChannel】 2 .register a selector 3.get the selectedKey(contains the ready channel) 4.get the channel and buffer 5. deal the buffer
    // server <- selector <-select ready channel <- channel <-buffer
    public static void main(String[] args) throws IOException {
        //create server and bind the port
        ServerSocketChannel scc=ServerSocketChannel.open();
        scc.bind(new InetSocketAddress(8080));
        //false means use nio model
        scc.configureBlocking(false);
        //create a selector
        Selector selector=Selector.open();
        //register selector to server and set the specific event eg. accept
        scc.register(selector, SelectionKey.OP_ACCEPT);

        Handler handler=new Handler();
        while(true){
            //the number of keys that are ready for operation
            if(selector.select(3000)==0){
                System.out.println("请求超时");
            }
            //get the real selected key
            Iterator<SelectionKey> iterator=selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                //SelectionKey contains the ready channel 【key.channel】and the selector【key.selector】
                SelectionKey key=iterator.next();

                if(key.isAcceptable()){
                    handler.handleAccept(key);
                }

                if(key.isReadable()){
                    handler.handleRead(key);
                }

                iterator.remove();
            }
        }
    }

    private static class Handler{
        private int buffersize=1024;
        private String localcharset="utf-8";

        public void handleAccept(SelectionKey key) throws IOException {
            SocketChannel sc=((ServerSocketChannel)key.channel()).accept();
            sc.configureBlocking(false);
            sc.register(key.selector(),SelectionKey.OP_READ, ByteBuffer.allocate(buffersize));
        }

        public void handleRead(SelectionKey key) throws IOException {
           SocketChannel sc= (SocketChannel)key.channel();
           //use the key to get the buffer key.attachment()
            ByteBuffer byteBuffer=(ByteBuffer)key.attachment();
            //reset the buffer,user before buffer.read;
            byteBuffer.clear();
            if(sc.read(byteBuffer)==-1){
            sc.close();
            }else{
                byteBuffer.flip();
                String receiving= Charset.forName(localcharset).newDecoder().decode(byteBuffer).toString();
                System.out.println("receiving msg:"+receiving);

                String sending="receiving data"+receiving;
                byteBuffer=ByteBuffer.wrap(sending.getBytes(localcharset));
                sc.write(byteBuffer);
                //close the socket
                sc.close();
            }
        }
    }
}
