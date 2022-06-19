package cn.emm233.provider;

import cn.emm233.common.codec.MessageDecoder;
import cn.emm233.common.codec.MessageEncoder;
import cn.emm233.config.ProviderProps;
import cn.emm233.provider.handler.ProxyClientHandler;
import cn.emm233.tcp.TcpConnection;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 描述：
 *
 * @author zhangchong
 * @date 2022/5/10 21:34
 */
@Slf4j
@Component
public class Provider implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Resource
    private ProviderProps providerProps;

    public void run() throws InterruptedException, IOException {

        String serverAddress = providerProps.getProxyHost();
        String serverPort = providerProps.getProxyPort();

        TcpConnection natxConnection = new TcpConnection();

        natxConnection.connect(serverAddress, Integer.parseInt(serverPort), new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {

                ch.pipeline().addLast(
                        new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                        new MessageDecoder(), new MessageEncoder(),
                        new IdleStateHandler(60, 30, 0),
                        applicationContext.getBean(ProxyClientHandler.class)
                );
            }
        });

        log.info("Provider启动成功");
    }
}
