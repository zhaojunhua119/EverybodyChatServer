import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by zhahua on 9/21/17.
 */
public class ChatServer {
  public ChatServer() {

  }
  public static void main(String args[]){
    new ChatServer().run();
  }
  public void run(){
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      ServerBootstrap bootstrap = new ServerBootstrap()
          .group(bossGroup,workerGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
              ChannelPipeline pipeline = socketChannel.pipeline();
              pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192,
                  Delimiters.lineDelimiter()));
              pipeline.addLast("decoder", new StringDecoder());
              pipeline.addLast("encoder", new StringEncoder());
              pipeline.addLast("handler", new ChatServerHandler());

            }
          });
      bootstrap.bind(8080).sync().channel().closeFuture().sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
