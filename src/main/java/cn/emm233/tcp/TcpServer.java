package cn.emm233.tcp;

import cn.emm233.common.ExposConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * TCP协议服务端
 *
 * @author zhangchong
 * @date 2019/2/27
 */
@Getter
@Setter
public class TcpServer {

    /**
     * 服务端监听channel
     */
    private Channel channel;

    /**
     * 监听端口
     */
    private final Integer port;

    /**
     * 暴露服务配置
     */
    private ExposConfig exposConfig;


    /**
     * 客户端channel
     */
    private ChannelGroup channelGroup;

    /**
     * 代理服务构造函数
     *
     * @param port 监听端口
     */
    public TcpServer(Integer port) {
        this.port = port;
    }

    /**
     * 暴露服务构造函数
     *
     * @param channelGroup
     * @param config
     */
    public TcpServer(ChannelGroup channelGroup, ExposConfig config) {
        this.port = config.getExposePort();
        this.channelGroup = channelGroup;
        this.exposConfig = config;

    }

    public synchronized void bind(ChannelInitializer channelInitializer) throws InterruptedException {

        EventLoopGroup bossGroup = new NioEventLoopGroup(4, new DefaultThreadFactory("bossGroup"));
        EventLoopGroup workerGroup = new NioEventLoopGroup(8, new DefaultThreadFactory("bossGroup"));

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            channel = b.bind(port).sync().channel();

            channel.closeFuture().addListener((ChannelFutureListener) future -> {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            });
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            throw e;
        }
    }

    /**
     * 关闭监听Channel
     */
    public synchronized void close() {
        if (Objects.nonNull(channel)) {
            channel.close();
        }
    }
}
