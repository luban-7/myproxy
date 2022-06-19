package cn.emm233.proxy;

import cn.emm233.common.codec.MessageDecoder;
import cn.emm233.common.codec.MessageEncoder;
import cn.emm233.config.ProxyProps;
import cn.emm233.proxy.handler.ProxyServerHandler;
import cn.emm233.tcp.TcpServer;
import cn.hutool.core.convert.Convert;
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

/**
 * 描述：
 *
 * @author zhangchong
 * @date 2022/5/10 21:33
 */
@Slf4j
@Component
public class ProxyServer implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Resource
    private ProxyProps proxyProps;

    public void run() throws InterruptedException {
        Integer port = Convert.convert(Integer.class, proxyProps.getProxyPort());
        TcpServer proxyServer = new TcpServer(port);

        proxyServer.bind(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(
                        new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                        new MessageDecoder(),
                        new MessageEncoder(),
                        new IdleStateHandler(60, 30, 0),
                        applicationContext.getBean(ProxyServerHandler.class)
                );
            }
        });

        log.info("ProxyServer启动成功");
    }
}
