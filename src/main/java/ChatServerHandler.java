import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhahua on 9/21/17.
 */
public class ChatServerHandler
  extends ChannelInboundHandlerAdapter
{
//  private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
  private static ConcurrentHashMap<String,Set<Channel>> roomToChannel = new ConcurrentHashMap<String,Set<Channel>>();

  private static ConcurrentHashMap<Channel,String> channelToRoom = new ConcurrentHashMap<Channel,String>();

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    Channel incoming = ctx.channel();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    Channel incoming = ctx.channel();
    String str = (String)msg;

    if(str.startsWith(":")) {
      String[] args = str.split(" ");
      args[0]=args[0].substring(1,args[0].length());
      if(args[0].equals("room")) {
        if(args.length != 2) {
          incoming.write("chating room name required");
          incoming.flush();
          return;
        }
        String oldRoom = channelToRoom.get(incoming);
        if(oldRoom != null) {
          incoming.write("you already joined " + oldRoom);
          incoming.flush();
        }
        String room = args[1];
        Set<Channel> channelSet = roomToChannel.get(room);
        if(channelSet == null) {
          channelSet = new HashSet<Channel>();
          roomToChannel.put(room, channelSet);
        }
        channelSet.add(incoming);
        channelToRoom.put(incoming,room);
      }
      return;
    }

    String room = channelToRoom.get(incoming);
    if(room != null) {
      Set<Channel> channels = roomToChannel.get(room);
      for (Channel channel : channels) {
        if (channel != incoming) {
          channel.write(incoming.id() + "@" + incoming.remoteAddress() + ": " + str + "\n");
          channel.flush();
        }
      }
    }

  }
}
