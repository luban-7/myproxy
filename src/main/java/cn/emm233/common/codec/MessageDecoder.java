package cn.emm233.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 描述：服务配置
 *
 * @author zhangchong
 * @date 2022/5/20 11:25
 */
public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List out) throws Exception {

        int type = msg.readInt();

        int metaDataLength = msg.readInt();
        CharSequence metaDataString = msg.readCharSequence(metaDataLength, CharsetUtil.UTF_8);
        JSONObject jsonObject = new JSONObject(metaDataString.toString());
        Map<String, Object> metaData = jsonObject.toMap();

        byte[] data = null;
        if (msg.isReadable()) {
            data = ByteBufUtil.getBytes(msg);
        }

        Message message = new Message();
        message.setType(type);
        message.setMetaData(metaData);
        message.setData(data);

        out.add(message);
    }

}
