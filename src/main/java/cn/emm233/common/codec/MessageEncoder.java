package cn.emm233.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * 描述：服务配置
 *
 * @author zhangchong
 * @date 2022/5/20 11:25
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {

            Integer commandType = msg.getType();
            dataOutputStream.writeInt(commandType);

            JSONObject metaDataJson = new JSONObject(msg.getMetaData());
            byte[] metaDataBytes = metaDataJson.toString().getBytes(CharsetUtil.UTF_8);
            dataOutputStream.writeInt(metaDataBytes.length);
            dataOutputStream.write(metaDataBytes);

            if (msg.getData() != null && msg.getData().length > 0) {
                dataOutputStream.write(msg.getData());
            }

            byte[] data = byteArrayOutputStream.toByteArray();
            out.writeInt(data.length);
            out.writeBytes(data);
        }

    }

}
